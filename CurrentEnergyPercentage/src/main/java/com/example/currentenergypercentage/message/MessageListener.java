package com.example.currentenergypercentage.message;

import com.example.currentenergypercentage.dto.EnergyUsageHourly;
import com.example.currentenergypercentage.repository.CurrentPercentageDatabaseRepository;
import com.example.currentenergypercentage.repository.CurrentPercentageEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    private final CurrentPercentageDatabaseRepository repository;

    public MessageListener(CurrentPercentageDatabaseRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "update")
    public void receiveProducer(String message) {
        System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        double community_depleted;
        double grid_portion;

        try {
            EnergyUsageHourly data = objectMapper.readValue(message, EnergyUsageHourly.class);

            if (data.getCommunityProduced() == 0 || data.getCommunityUsed() > data.getCommunityProduced()) {
                community_depleted = 100;
            } else {
                community_depleted = data.getCommunityUsed()/data.getCommunityProduced()*100;
            }

            if (data.getCommunityUsed() == 0) {
                grid_portion = 0;
            } else {
                grid_portion = data.getGritUsed()/data.getCommunityUsed()*100;
            }

            CurrentPercentageEntity sql = new CurrentPercentageEntity(data.getTimestamp(), community_depleted, grid_portion);
            repository.save(sql);
            System.out.println(sql);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
