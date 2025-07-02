package org.example.communityenergyproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;

/**
 * EnergyProducer, der alle 1-5 Sekunden realistische Erzeugungswerte an eine RabbitMQ Qeue sendet
 * @Author Laurin
 * @Version 02.07.2025
 */

@Service
public class EnergyProducerController {

    private final RabbitTemplate rabbit;
    private final RestTemplate rest = new RestTemplate();
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TaskScheduler scheduler;

    public EnergyProducerController(RabbitTemplate rabbit, TaskScheduler scheduler) {
        this.rabbit = rabbit;
        this.scheduler = scheduler;
    }

    @PostConstruct // Ausgeführt nach Bean Initialisierung
    public void startScheduling() {
        scheduleNextRun();
    }

    /**
     * KEIN Thread.sleep() weil:
     * Blockiert den aktuellen Thread vollständig für die angegebene Zeit
     * Fehleranfällig (Exception handling)
     * Verringerte Effizienz bei Skalierung wegen künstlichem Abbruch
     */
    private void scheduleNextRun() {
        int delay = 1000 + random.nextInt(4000); // 1–5 Sekunden
        scheduler.schedule(this::fetchAndSendProduction, new Date(System.currentTimeMillis() + delay)); // :: Lambda kurzversion
    }

    // @Scheduled(fixedRate = 5000) // alle 5 Sekunden, alte vorgehensweise
    public void fetchAndSendProduction() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=48.2&longitude=16.3&current_weather=true&daily=sunrise,sunset&timezone=Europe/Vienna";
        try {
            Map<String, Object> response = rest.getForObject(url, Map.class);

            // Wetterdaten auslesen
            Map<String, Object> currentWeather = (Map<String, Object>) response.get("current_weather");
            Map<String, Object> daily = (Map<String, Object>) response.get("daily");

            int weatherCode = ((Number) currentWeather.get("weathercode")).intValue();

            // Sonnenaufgang & -untergang extrahieren
            String sunriseStr = ((List<String>) daily.get("sunrise")).get(0); // "2025-06-17T04:58"
            String sunsetStr = ((List<String>) daily.get("sunset")).get(0);   // "2025-06-17T20:58"

            LocalDateTime sunrise = LocalDateTime.parse(sunriseStr);
            LocalDateTime sunset = LocalDateTime.parse(sunsetStr);
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Vienna"));

            // Wetterfaktor festlegen für spätere Berechnung
            double weatherFactor;
            if (weatherCode <= 1) {
                weatherFactor = 1.0; // sonnig/klar
            } else if (weatherCode <= 3) {
                weatherFactor = 0.6; // bewölkt
            } else {
                weatherFactor = 0.2; // Regen/Nebel etc.
            }

            double production;
            if (now.isBefore(sunrise) || now.isAfter(sunset)) {
                // Nachts: keine Produktion
                production = 0.0;
            } else if (now.isAfter(sunrise.plusHours(3)) && now.isBefore(sunset.minusHours(3))) {
                // Mittagszeit: höchste Produktion
                production = weatherFactor * (1.0 + random.nextDouble()); // 1.0–2.0 kWh
            } else {
                // Vormittag / Spätnachmittag: reduzierte Produktion
                production = weatherFactor * (0.3 + random.nextDouble() * 0.5); // 0.3–0.8 kWh
            }

            // Nachricht als JSON zusammenbauen
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("type", "PRODUCER");
            messageMap.put("association", "COMMUNITY");
            messageMap.put("produced", production);
            messageMap.put("datetime", now.toString());

            String messageJson = objectMapper.writeValueAsString(messageMap);
            rabbit.convertAndSend("com_energy_producer", messageJson); // An Qeue senden

            System.out.println("Sent production: " + messageJson + " (WCode: " + weatherCode + ")");

        } catch (Exception e) {
            System.err.println("Senden fehlgeschlagen: " + e.getMessage());
        }
        scheduleNextRun();
    }
}
