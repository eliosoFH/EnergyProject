package org.example.usageservice.message;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageListener {

    private final EnergyUsageDatabaseRepository repository;
    private final RabbitTemplate rabbit;
    public MessageListener(EnergyUsageDatabaseRepository repository, RabbitTemplate rabbit) {
        this.repository = repository;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = {"com_energy_producer", "com_energy_user"})
    public void receiveProducer(String message)  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EnergyUsageHourlyEntity newData = objectMapper.readValue(message, EnergyUsageHourlyEntity.class);

            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1);
            EnergyUsageHourlyEntity currentData = repository.findById(now).orElse(null);

            if (currentData == null) {
                currentData = new EnergyUsageHourlyEntity();
                currentData.setHour(newData.getHour().withMinute(0).withSecond(0).withNano(0).plusHours(1));
            }

            currentData.setCommunityUsed(currentData.getCommunityUsed() + newData.getCommunityUsed());
            currentData.setCommunityProduced(currentData.getCommunityProduced() + newData.getCommunityProduced());
            if ((currentData.getCommunityUsed()-currentData.getCommunityProduced()) > 0) {
                currentData.setGridUsed(currentData.getCommunityUsed()-currentData.getCommunityProduced());
            }

            repository.save(currentData);

            rabbitPush(currentData);

        } catch (Exception e) {
            System.out.println("Fehler aufgetreten!");
            e.printStackTrace();
        }
    }

    private void rabbitPush(EnergyUsageHourlyEntity currentData) {
        try {
            // send energyStats to queue
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("produced", currentData.getCommunityProduced());
            messageMap.put("used", currentData.getCommunityUsed());
            messageMap.put("grid", currentData.getGridUsed());
            messageMap.put("timestamp", currentData.getHour());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String messageJson = objectMapper.writeValueAsString(messageMap);
            rabbit.convertAndSend("update", messageJson);
            System.out.println("updated" + currentData);

        } catch (Exception e) {
            System.out.println(e);
        }
    }





}
