package org.example.communityenergyuser.Controller;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Random;

@Service
public class EnergyUserController {

    private final RabbitTemplate rabbit;
    private final Random random = new Random();

    public EnergyUserController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Scheduled(fixedRate = 5000) // alle 5 Sekunden
    public void sendRandomUsage() {
        double baseUsage = generateUsageDependingOnTime();
        String message = String.format("%.2f", baseUsage); // baseUsage auf zwei Nachkommastellen
        rabbit.convertAndSend("com_energy_user", message);
        System.out.println("Sent usage: " + message + " kWh");
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


