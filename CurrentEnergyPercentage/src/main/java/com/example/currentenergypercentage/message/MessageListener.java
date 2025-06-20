package com.example.currentenergypercentage.message;

import com.example.currentenergypercentage.dto.EnergyStats;
import com.example.currentenergypercentage.repository.CurrentPercentageDatabaseRepository;
import com.example.currentenergypercentage.repository.CurrentPercentageEntity;
import com.example.currentenergypercentage.repository.EnergyUsageDatabaseRepository;
import com.example.currentenergypercentage.repository.EnergyUsageHourlyEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    private final CurrentPercentageDatabaseRepository repository;

    public MessageListener(CurrentPercentageDatabaseRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "com_energy_producer")
    public void receiveProducer(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EnergyStats data = objectMapper.readValue(message, EnergyStats.class);

            double community_depleted = data.getCommunityUsed()/data.getCommunityProduced()*100;
            double grid_portion = data.getGritUsed()/data.getCommunityUsed();

            CurrentPercentageEntity sql = new CurrentPercentageEntity(data.getTimestamp(), community_depleted, grid_portion);
            repository.save(sql);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
