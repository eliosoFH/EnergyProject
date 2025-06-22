package com.example.BackEndAPI.Controller;


import com.example.BackEndAPI.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final CurrentPercentageDatabaseRepository currentDataRepository;
    private final EnergyUsageDatabaseRepository energyDatabaseRepository; // DB

    public EnergyController(CurrentPercentageDatabaseRepository currentDataRepository, EnergyUsageDatabaseRepository energyDatabaseRepository) {
        this.currentDataRepository = currentDataRepository;
        this.energyDatabaseRepository = energyDatabaseRepository;
    }

    @GetMapping("/current")
    public CurrentPercentageEntity getCurrentHour() {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        return currentDataRepository.findById(now).orElse(null);
    }

    @GetMapping("/historic")
    public List<EnergyUsageHourlyEntity> getHistoricData(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) übergabe string wird zu datetime objekt umgewandelt

        return energyDatabaseRepository.findAll().stream()
                .filter(e -> e.getHour().isAfter(start) && !e.getHour().isAfter(end))
                // Stunde 16 zählt für Werte 15-16:00
                .sorted(Comparator.comparing(EnergyUsageHourlyEntity::getHour))
                .toList();

    }
}
