package org.example.usageservice.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testklasse für den MessageListener.
 * Diese Klasse prüft, ob eine vom Producer eingehende Nachricht korrekt verarbeitet wird.
 * Dazu zählt auch das Speichern in der Datenbank und weiterleiten der Nachricht über RabbitMQ.
 */
class MessageListenerTest {

    // Repository zum Speichern der Energiedaten in die Datenbank
    private EnergyUsageDatabaseRepository repository;
    // RabbitTemplate zum Senden von Nachrichten, in diesem Fall eine Antwort.s
    private RabbitTemplate rabbit;
    // Der MessageListener, dessen Methode getestet wird
    private MessageListener listener;

    /**
     * Setup, welches vor jedem Testfall ausgeführt wird.
     * Erstellt Mock-Objekte und initialisiert den MessageListener.
     */
    @BeforeEach
    void setUp() {
        repository = mock(EnergyUsageDatabaseRepository.class);
        rabbit = mock(RabbitTemplate.class);
        listener = new MessageListener(repository, rabbit);
    }

    /**
     * Testet die Methode receiveProducer().
     * Es wird geprüft, ob die eingehende PRODUCER-Message verarbeitet, korrekt gespeichert und anschließend über RabbitMQ weitergeleitet wird.
     */
    @Test
    void testReceiveProducerMessage() throws Exception {
        // Erzeugt eine auf eine volle Stunde gerundete Uhrzeit.
        LocalDateTime hour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // Erstellt eine Entity, einen "Datendummy", mit Verbrauchs- und Produktionswerten sowie Uhrzeit
        EnergyUsageHourlyEntity entity = new EnergyUsageHourlyEntity();
        entity.setCommunityUsed(2.0);
        entity.setCommunityProduced(1.5);
        entity.setHour(hour);

        // Wandelt das Entity Object in einen JSON-String um, sowie man es sonst von RabbitMQ erhält.
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(entity);

        // Simuliert, dass zur nächsten Stunde noch kein Eintrag in der DB existiert
        when(repository.findById(eq(hour.plusHours(1)))).thenReturn(Optional.empty());

        // Ruft receiveProducer Methode mittels erstellem JSON auf.
        // Simuliert eine eingetroffen Nachricht.
        listener.receiveProducer(json);

        // Fängt das Entity Object ab, welches in die DB gespeichert werden soll
        ArgumentCaptor<EnergyUsageHourlyEntity> entityCaptor = ArgumentCaptor.forClass(EnergyUsageHourlyEntity.class);
        verify(repository).save(entityCaptor.capture());

        // Holt das gespeicherte Object aus dem ArgumentCaptor
        EnergyUsageHourlyEntity saved = entityCaptor.getValue();

        // Prüft ob die Verbrachsdaten mit den anfangs gesetzten Daten übereinstimmen.
        assertEquals(2.0, saved.getCommunityUsed());
        // Prüft ob die Produktionswert mit den anfangs gesetzten Daten übereinstimmen.
        assertEquals(1.5, saved.getCommunityProduced());
        // Prüft ob die Differenz (der Netzverbrauch) mit den anfangs gesetzten Daten übereinstimmen.
        assertEquals(0.5, saved.getGridUsed(), 0.0001);

        // Prüft, ob eine Nachricht an die "update"-Queue gesendet wurde
        verify(rabbit).convertAndSend(eq("update"), any(String.class));
    }


}
