package org.example.communityenergyproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class EnergyProducerController {

    private final RabbitTemplate rabbit;
    private final RestTemplate rest = new RestTemplate();
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EnergyProducerController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Scheduled(fixedRate = 5000) // alle 5 Sekunden
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

            // Wetterfaktor bestimmen
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
            rabbit.convertAndSend("com_energy_producer", messageJson);

            System.out.println("Sent production: " + messageJson + " (WCode: " + weatherCode + ")");

        } catch (Exception e) {
            System.err.println("Senden fehlgeschlagen: " + e.getMessage());
        }
    }
}
