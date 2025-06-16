package org.example.communityenergyuser;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CommunityEnergyUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityEnergyUserApplication.class, args);
    }

    @Bean
    public Queue userQueue() {
        return new Queue("com_energy_producer", true); // durable = true (dami
    }

}
