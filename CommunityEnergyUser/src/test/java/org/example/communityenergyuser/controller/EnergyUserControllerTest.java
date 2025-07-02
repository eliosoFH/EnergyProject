package org.example.communityenergyuser.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testklasse für den EnergyUserController.
 * Diese Klasse überprüft, ob beim Senden vom CommunityEnergyUser eine korrekt formatierte Nachricht an RabbitMQ gesendet wird.
 */
class EnergyUserControllerTest {

    // RabbitTemplate zum Senden von Nachrichten
    private RabbitTemplate rabbitTemplate;
    // TaskScheduler wird hier NICHT getestet - ist vorraussetzung im Konstruktor
    private TaskScheduler scheduler;
    // Das Controller Object dessen Funktionen getestet werden sollen
    private org.example.communityenergyuser.Controller.EnergyUserController controller;

    /**
     * Setup, welches vor jedem Testfall ausgeführt wird.
     * Erstellt Mock-Objekte und initialisiert den Controller.
     */

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        scheduler = mock(TaskScheduler.class);
        controller = new org.example.communityenergyuser.Controller.EnergyUserController(rabbitTemplate, scheduler);
    }

    /**
     * Testet die Methode sendRandomUsage().
     * Es wird geprüft, ob eine korrekt formatierte Nachricht an RabbitMQ gesendet wird.
     */
    @Test
    void testSendUsageMessage() {
        // Zu testende Methode
        controller.sendRandomUsage();

        // Erstelle einen ArgumentCaptor, um die tatsächlich gesendete Nachricht abzufangen
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        // Prüft ob convertAndSend aufgerufen wurde und fängt die übergebene Nachricht ab
        verify(rabbitTemplate).convertAndSend(eq("com_energy_user"), messageCaptor.capture());
        //Speichert die Nachricht in einen String, der auf seine formatierung geprüft werden soll.
        String message = messageCaptor.getValue();

        // Prüft zu erst ob die abgefangenen Message nicht Null ist.
        assertNotNull(message);
        //Prüft ob die gesendete Nachricht "USER" korrekt für Type enthält.
        assertTrue(message.contains("USER"));
        //Prüft ob in der gesendeteten Nachricht der Verbrauchte Strom enthalten ist
        assertTrue(message.contains("used"));
        //Prüft ob ein Zeitstempel in der Nachricht enthalten ist
        assertTrue(message.contains("datetime"));
    }
}
