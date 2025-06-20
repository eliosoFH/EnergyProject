package com.example.currentenergypercentage;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CurrentEnergyPercentageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrentEnergyPercentageApplication.class, args);
    }
    @Bean
    public Queue updateQueue() {
        return new Queue("com_energy_user", true);
    }
}
