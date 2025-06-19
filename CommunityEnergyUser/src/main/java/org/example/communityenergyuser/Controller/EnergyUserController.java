package org.example.communityenergyuser.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EnergyUserController {

    private final RabbitTemplate rabbit;
    private final Random random = new Random();

    public EnergyUserController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Scheduled(fixedRate = 5000) // alle 5 Sekunden
    public void sendRandomUsage() throws JsonProcessingException {
        double baseUsage = generateUsageDependingOnTime();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type", "USER");
        messageMap.put("association", "COMMUNITY");
        messageMap.put("kwh", baseUsage);
        messageMap.put("datetime", LocalDateTime.now(ZoneId.of("Europe/Vienna")).toString());


        String messageJson = objectMapper.writeValueAsString(messageMap);
        rabbit.convertAndSend("com_energy_user", messageJson);
        System.out.println("Sent usage: " + messageJson);
    }

    private double generateUsageDependingOnTime() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        double base = 0.2 + (0.3 * random.nextDouble()); // Basisverbrauch 0.2–0.5 kWh

        /**
         * Nacht: 22–5 Uhr (0.1 - 0.2)
         * Morgen: 6–9 Uhr (0.7 - 1.3)
         * Abend: 17–21 Uhr (0.9 - 1.7)
         * Tagsüber: 10–16 Uhr (0.2 - 0.5)
         */
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


