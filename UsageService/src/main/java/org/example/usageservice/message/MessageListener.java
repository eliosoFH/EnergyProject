package org.example.usageservice.message;

import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageListener {

    private final EnergyUsageDatabaseRepository repository;

    public MessageListener(EnergyUsageDatabaseRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "com_energy_producer")
    public void receiveProducer(String message) {
        String[] parts = message.split(";");
        String hourString = parts[1];
        double energy = Double.parseDouble(parts[2]);

        LocalDateTime hour = LocalDateTime.parse(hourString).withMinute(0).withSecond(0).withNano(0);
        EnergyUsageHourlyEntity entity = repository.findById(hour)
                .orElse(new EnergyUsageHourlyEntity(hour, 0.0, 0.0));
        entity.setCommunityProduced(entity.getCommunityProduced() + energy);
        repository.save(entity);
    }

    @RabbitListener(queues = "com_energy_user")
    public void receiveUser(String message) {
        String[] parts = message.split(";");
        String hourString = parts[1];
        double communityUsed = Double.parseDouble(parts[2]);
        double gridUsed = 0.0;

        LocalDateTime hour = LocalDateTime.parse(hourString).withMinute(0).withSecond(0).withNano(0);
        EnergyUsageHourlyEntity entity = repository.findById(hour)
                .orElse(new EnergyUsageHourlyEntity(hour, 0.0, 0.0));
        entity.setCommunityUsed(entity.getCommunityUsed() + communityUsed);
        entity.setGridUsed(entity.getGridUsed() + gridUsed);
        repository.save(entity);
    }
}
