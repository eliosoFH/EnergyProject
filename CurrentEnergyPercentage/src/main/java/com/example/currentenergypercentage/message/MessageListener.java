package com.example.currentenergypercentage.message;

import com.example.currentenergypercentage.repository.EnergyUsageDatabaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    private final EnergyUsageDatabaseRepository repository;

    public MessageListener(EnergyUsageDatabaseRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "com_energy_producer")
    public void receiveProducer(String message) {

    }

}
