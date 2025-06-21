package com.example.BackEndAPI.repository;


import com.example.BackEndAPI.dto.EnergyUsageHourly;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EnergyRepository {
    private final Map<LocalDateTime, EnergyUsageHourly> energieByHour = new HashMap<>(Map.of(
            LocalDateTime.of(2024, 1, 25, 15, 0),
            new EnergyUsageHourly(LocalDateTime.of(2024, 1, 25, 15, 0), 10.5, 8.2, 1.1),
            LocalDateTime.of(2024, 2, 25, 14, 0),
            new EnergyUsageHourly(LocalDateTime.of(2024, 2, 25, 14, 0), 12.0, 10.0, 0.8),
            LocalDateTime.of(2024, 3, 25, 13, 0),
            new EnergyUsageHourly(LocalDateTime.of(2024, 3, 25, 13, 0), 9.3, 9.5, 1.2),
            LocalDateTime.of(2024, 4, 25, 12, 0),
            new EnergyUsageHourly(LocalDateTime.of(2024, 4, 25, 12, 0), 15.1, 14.0, 0.0),
            LocalDateTime.now().withMinute(0).withSecond(0).withNano(0),
            new EnergyUsageHourly(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0), 20.0, 20.0, 2.0)
    ));

    public EnergyUsageHourly getCurrent() {
        LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        return energieByHour.getOrDefault(nowHour, new EnergyUsageHourly(nowHour, 0, 0, 0));
    }

    public List<EnergyUsageHourly> getHistoric(LocalDateTime start, LocalDateTime end) {
        return energieByHour.values().stream()
                .filter(e -> !e.getHour().isBefore(start) && !e.getHour().isAfter(end))
                .sorted(Comparator.comparing(EnergyUsageHourly::getHour))
                .toList();
    }

}
