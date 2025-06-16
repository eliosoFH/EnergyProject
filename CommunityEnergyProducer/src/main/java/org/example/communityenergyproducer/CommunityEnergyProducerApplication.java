package org.example.communityenergyproducer;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CommunityEnergyProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityEnergyProducerApplication.class, args);
    }

    @Bean
    public Queue userQueue() {
        return new Queue("com_energy_producer", true);  // durable = true (dauerhaft speichern)
    }

}
