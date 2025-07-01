package org.example.communityenergyproducer;


import org.example.communityenergyproducer.controller.EnergyProducerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class EnergyProducerApplicationTest {

    private RabbitTemplate rabbitTemplate;
    private TaskScheduler scheduler;
    private EnergyProducerController controller;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        scheduler = mock(TaskScheduler.class);
        controller = new EnergyProducerController(rabbitTemplate, scheduler);
    }

    @Test
    void testSendProductionMessage() {

        controller.fetchAndSendProduction();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate).convertAndSend(eq("com_energy_producer"), messageCaptor.capture());

        String message = messageCaptor.getValue();
        assertNotNull(message);
        assertTrue(message.contains("PRODUCER"));
        assertTrue(message.contains("produced"));
        assertTrue(message.contains("datetime"));
    }
}
