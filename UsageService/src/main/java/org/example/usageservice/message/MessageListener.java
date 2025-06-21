package org.example.usageservice.message;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.usageservice.dto.EnergyUsageHourly;
import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageListener {

    private final EnergyUsageDatabaseRepository repository;
    private final RabbitTemplate rabbit;
    public MessageListener(EnergyUsageDatabaseRepository repository, RabbitTemplate rabbit) {
        this.repository = repository;
        this.rabbit = rabbit;
    }

    List<EnergyUsageHourly> list = new ArrayList<>();

    boolean producedHourOver = false;
    boolean usedHourOver = false;

    double excessProduced = 0;
    double excessUsed = 0;

    private EnergyUsageHourly energyStats = new EnergyUsageHourly();

    @RabbitListener(queues = "com_energy_producer")
    public void receiveProducer(String message)  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EnergyUsageHourly data = objectMapper.readValue(message, EnergyUsageHourly.class);

            initTime(data.getTimestamp());

            if(data.getTimestamp().getHour() == energyStats.getTimestamp().getHour()) {
                energyStats.setCommunityProduced(energyStats.getCommunityProduced()+ data.getCommunityProduced());
                System.out.println("Produced: " + energyStats);
            } else {
                excessProduced += data.getCommunityProduced();
                producedHourOver = true;
                if (usedHourOver && producedHourOver) {
                    dbAndRabbit();
                }
            }

        } catch (Exception e) {
            System.out.println("Fehler aufgetreten!");
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "com_energy_user")
    public void receiveUser(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EnergyUsageHourly data = objectMapper.readValue(message, EnergyUsageHourly.class);

            initTime(data.getTimestamp());

            if (data.getTimestamp().getHour() == energyStats.getTimestamp().getHour()) {
                energyStats.setCommunityUsed(energyStats.getCommunityUsed() + data.getCommunityUsed());
                System.out.println("Used: " + energyStats);
            } else {
                excessUsed += data.getCommunityUsed();
                usedHourOver = true;
                if (usedHourOver && producedHourOver) {
                    dbAndRabbit();
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler aufgetreten!");
        }
    }

    private void dbAndRabbit() {
        energyStats.setGritUsed(energyStats.getCommunityUsed()-energyStats.getCommunityProduced());
        if (energyStats.getGritUsed()<0) {
            energyStats.setGritUsed(0);
        }

        System.out.println("DB: " + energyStats);

        EnergyUsageHourlyEntity sql = new EnergyUsageHourlyEntity(energyStats.getTimestamp().plusHours(1), energyStats.getCommunityUsed(), energyStats.getCommunityProduced(), energyStats.getGritUsed());
        repository.save(sql);

        try {
            // send energyStats to queue
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("produced", energyStats.getCommunityProduced());
            messageMap.put("used", energyStats.getCommunityUsed());
            messageMap.put("grid", energyStats.getGritUsed());
            messageMap.put("timestamp", energyStats.getTimestamp().plusHours(1));

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String messageJson = objectMapper.writeValueAsString(messageMap);
            rabbit.convertAndSend("update", messageJson);
            System.out.println("updated");

        } catch (Exception e) {
            System.out.println(e);
        }
        energyStats = new EnergyUsageHourly();
        energyStats.setCommunityUsed(excessUsed);
        energyStats.setCommunityProduced(excessProduced);
        excessUsed = 0;
        excessProduced = 0;
    }

    private void initTime(LocalDateTime date) {
        if(energyStats.getTimestamp() == null) {
            energyStats.setTimestamp(date.withMinute(0).withSecond(0).withNano(0));
        }
    }
}
