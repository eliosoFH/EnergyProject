package org.example.usageservice;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsageServiceApplication.class, args);
	}

	@Bean
	public Queue userQueue() {
		return new Queue("user_queue", true); // durable = true
	}

	@Bean
	public Queue producerQueue() {
		return new Queue("producer_queue", true);
	}

	@Bean
	public Queue updateQueue() {
		return new Queue("update_queue", true);
	}

}
