package org.example.usageservice.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.usageservice.dto.EnergyStats;
import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.sql.SQLOutput;
import java.sql.Timestamp;
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

    List<EnergyStats> list = new ArrayList<>();

    boolean producedHourOver = false;
    boolean usedHourOver = false;

    double excessProduced = 0;
    double excessUsed = 0;

    private EnergyStats energyStats = new EnergyStats();

    @RabbitListener(queues = "com_energy_producer")
    public void receiveProducer(String message)  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EnergyStats data = objectMapper.readValue(message, EnergyStats.class);

            initTime(data.getTimestamp());

            if(data.getTimestamp().getHour() == energyStats.getTimestamp().getHour()) {
                energyStats.setCommunityProduced(energyStats.getCommunityProduced()+ data.getCommunityProduced());
            } else {
                excessProduced += data.getCommunityProduced();
                producedHourOver = true;
                if (usedHourOver && producedHourOver) {
                    postDB();
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
            EnergyStats data = objectMapper.readValue(message, EnergyStats.class);

            initTime(data.getTimestamp());

            if (data.getTimestamp().getHour() == energyStats.getTimestamp().getHour()) {
                energyStats.setCommunityUsed(energyStats.getCommunityUsed() + data.getCommunityUsed());
            } else {
                excessUsed += data.getCommunityUsed();
                usedHourOver = true;
                if (usedHourOver && producedHourOver) {
                    postDB();
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler aufgetreten!");
        }
        System.out.println(energyStats);
    }

    private void postDB() {
        energyStats.setGritUsed(energyStats.getCommunityUsed()- energyStats.getCommunityProduced());
        EnergyUsageHourlyEntity sql = new EnergyUsageHourlyEntity(energyStats.getTimestamp().plusHours(1), energyStats.getCommunityUsed(), energyStats.getCommunityProduced());
        repository.save(sql);
        System.out.println("jallo");

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
            System.out.println("hallo");

        } catch (Exception e) {
            System.out.println(e);
        }
        energyStats = new EnergyStats();
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
