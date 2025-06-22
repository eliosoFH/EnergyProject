package org.example.communityenergyuser.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EnergyUserController {

    private final RabbitTemplate rabbit;
    private final TaskScheduler scheduler;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EnergyUserController(RabbitTemplate rabbit, TaskScheduler scheduler) {
        this.rabbit = rabbit;
        this.scheduler = scheduler;
    }

    @PostConstruct // Automatisch ausgeführt nach BEAN Erstellung, besser als im Konstruktor
    public void startScheduling() {
        scheduleNextRun();
    }

    private void scheduleNextRun() {
        int delay = 1000 + random.nextInt(4000); // 1–5 Sekunden
        scheduler.schedule(this::sendRandomUsage, new java.util.Date(System.currentTimeMillis() + delay));
    }

    public void sendRandomUsage() {
        try {
            double usage = generateUsageDependingOnTime();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("type", "USER");
            messageMap.put("association", "COMMUNITY");
            messageMap.put("used", usage);
            messageMap.put("datetime", LocalDateTime.now(ZoneId.of("Europe/Vienna")).toString());

            String messageJson = objectMapper.writeValueAsString(messageMap);
            rabbit.convertAndSend("com_energy_user", messageJson);

            System.out.println("Sent usage: " + messageJson);
        } catch (Exception e) {
            System.err.println("Senden fehlgeschlagen: " + e.getMessage());
        }

        scheduleNextRun(); // nächstes Scheduling einplanen
    }

    private double generateUsageDependingOnTime() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        double base = 0.2 + (0.3 * random.nextDouble()); // Basisverbrauch 0.2–0.5 kWh

        // Verbrauch je nach Tageszeit anpassen
        if (hour >= 6 && hour < 10) { // Morgens
            return base + 0.5 + (0.3 * random.nextDouble());
        } else if (hour >= 17 && hour < 22) { // Abends
            return base + 0.7 + (0.5 * random.nextDouble());
        } else if (hour < 6 || hour >= 22) { // Nachts
            return 0.1 + (0.1 * random.nextDouble());
        } else {
            return base;
        }
    }
}
