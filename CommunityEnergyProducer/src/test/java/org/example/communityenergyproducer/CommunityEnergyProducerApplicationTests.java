package org.example.communityenergyproducer;

import org.example.communityenergyproducer.controller.EnergyProducerController;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommunityEnergyProducerApplicationTest {

    @Autowired
    private Queue userQueue;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    void contextLoads() {
        assertNotNull(userQueue, "Queue should be initialized");
        assertEquals("com_energy_producer", userQueue.getName(), "Queue name should match");
        assertTrue(userQueue.isDurable(), "Queue should be durable");

        assertNotNull(taskScheduler, "TaskScheduler should be initialized");
        assertTrue(taskScheduler.getActiveCount() >= 0, "Scheduler thread pool should be active");
    }
}
