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

    @GetMapping("/historic/{id}")
    public Energy getHistoricData(@PathVariable("id") int index) {
        return energyRepository.getHistoric(index);
    }

}
