package org.example.communityenergyproducer.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

/**
 * Testklasse für den EnergyProducerController.
 * Diese Klasse überprüft, ob beim Senden vom CommunityEnergyProducer eine korrekt formatierte Nachricht an RabbitMQ gesendet wird.
 */
class EnergyProducerControllerTest {

    // RabbitTemplate zum Senden von Nachrichten
    private RabbitTemplate rabbitTemplate;
    // TaskScheduler wird hier NICHT getestet - ist vorraussetzung im Konstruktor
    private TaskScheduler scheduler;
    // Das Controller Object dessen Funktionen getestet werden sollen
    private EnergyProducerController controller;

    /**
     * Setup, welches vor jedem Testfall ausgeführt wird.
     * Erstellt Mock-Objekte und initialisiert den Controller.
     */
    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        scheduler = mock(TaskScheduler.class);
        controller = new EnergyProducerController(rabbitTemplate, scheduler);
    }

    /**
     * Testet die Methode fetchAndSendProduction().
     * Es wird geprüft, ob eine korrekt formatierte Nachricht an RabbitMQ gesendet wird.
     */
    @Test
    void testSendProductionMessage() {
        // Zu testende Methode
        controller.fetchAndSendProduction();

        // Erstelle einen ArgumentCaptor, um die tatsächlich gesendete Nachricht abzufangen
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        // Prüft ob convertAndSend aufgerufen wurde und fängt die übergebene Nachricht ab
        verify(rabbitTemplate).convertAndSend(eq("com_energy_producer"), messageCaptor.capture());

        //Speichert die Nachricht in einen String, der auf seine formatierung geprüft werden soll.
        String message = messageCaptor.getValue();

        // Prüft zu erst ob die abgefangenen Message nicht Null ist.
        assertNotNull(message);
        //Prüft ob die gesendete Nachricht "PRODUCER" korrekt für Type enthält.
        assertTrue(message.contains("PRODUCER"));
        //Prüft ob in der gesendeteten Nachricht der produzierte Strom enthalten ist
        assertTrue(message.contains("produced"));
        //Prüft ob ein Zeitstempel in der Nachricht enthalten ist
        assertTrue(message.contains("datetime"));
    }
}
