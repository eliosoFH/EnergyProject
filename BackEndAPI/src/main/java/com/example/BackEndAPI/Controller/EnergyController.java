package com.example.BackEndAPI.Controller;


import com.example.BackEndAPI.dto.Energy;
import com.example.BackEndAPI.repository.EnergyRepository;
import com.example.BackEndAPI.repository.EnergyUsageDatabaseRepository;
import com.example.BackEndAPI.repository.EnergyUsageHourlyEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyRepository energyRepository;
    private final EnergyUsageDatabaseRepository energyDatabaseRepository; // DB

    public EnergyController(
            EnergyRepository energyRepository,
            EnergyUsageDatabaseRepository energyDatabaseRepository
    ) {
        this.energyRepository = energyRepository;
        this.energyDatabaseRepository = energyDatabaseRepository;
    }

    @GetMapping("/current")
    public EnergyUsageHourlyEntity getCurrentHour() {

        // dummy return energyRepository.getCurrent();

        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        return energyDatabaseRepository.findById(now).orElse(null);
    }

    @GetMapping("/historic")
    public List<EnergyUsageHourlyEntity> getHistoricData(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) übergabe string wird zu datetime objekt umgewandelt
        // dummy return energyRepository.getHistoric(start, end);
        return energyDatabaseRepository.findAll().stream()
        .filter(e -> !e.getHour().isBefore(start) && !e.getHour().isAfter(end))
        .sorted(Comparator.comparing(EnergyUsageHourlyEntity::getHour))
        .toList();
    }
}
