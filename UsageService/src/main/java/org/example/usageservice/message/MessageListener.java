package org.example.usageservice.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.usageservice.dto.EnergyStats;
import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageListener {

    private final EnergyUsageDatabaseRepository repository;
    public MessageListener(EnergyUsageDatabaseRepository repository) {
        this.repository = repository;
    }

    List<EnergyStats> list = new ArrayList<>();

    boolean producedHourOver = false;
    boolean usedHourOver = false;

    private EnergyStats energyStats = new EnergyStats();

    @RabbitListener(queues = "com_energy_producer")
    public void receiveProducer(String message)  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EnergyStats data = objectMapper.readValue(message, EnergyStats.class);

            if(energyStats.getTimestamp() == null) {
                energyStats.setTimestamp(data.getTimestamp().withMinute(0).withSecond(0).withNano(0));
            }

            if(data.getTimestamp().getHour() == energyStats.getTimestamp().getHour()) {
                energyStats.setCommunityProduced(energyStats.getCommunityProduced()+ data.getCommunityProduced());
            } else {
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

            if(energyStats.getTimestamp() == null) {
                energyStats.setTimestamp(data.getTimestamp().withMinute(0).withSecond(0).withNano(0));
            }

            if (data.getTimestamp().getHour() == energyStats.getTimestamp().getHour()) {
                energyStats.setCommunityUsed(energyStats.getCommunityUsed() + data.getCommunityUsed());
            } else {
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
        energyStats = new EnergyStats();
    }
}
