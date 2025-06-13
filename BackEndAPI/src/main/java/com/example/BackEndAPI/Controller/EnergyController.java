package com.example.BackEndAPI.Controller;


import com.example.BackEndAPI.dto.Energy;
import com.example.BackEndAPI.repository.EnergyRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyRepository energyRepository;

    public EnergyController(EnergyRepository energyRepository) {
        this.energyRepository = energyRepository;
    }

    @GetMapping("/current")
    public Energy getCurrentHour() {
        return energyRepository.getCurrent();
    }

    @GetMapping("/historic")
    public List<Energy> getHistoricData(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) übergabe string wird zu datetime objekt umgewandelt

        return energyRepository.getHistoric(start, end);
    }
}
