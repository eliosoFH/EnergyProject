package com.example.BackEndAPI.repository;


import com.example.BackEndAPI.dto.Energy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnergyRepository {
    private Map<Integer, Energy> energieByTime = new HashMap<>(Map.of(
            1, new Energy(1, LocalDateTime.of(2024, 1, 25, 15, 30), 14),
            2, new Energy(2, LocalDateTime.of(2024, 2, 25, 15, 30), 1241),
            3, new Energy(3, LocalDateTime.of(2024, 3, 25, 15, 30), 1240),
            4, new Energy(4, LocalDateTime.of(2024, 4, 25, 15, 30), 1040),
            5, new Energy(5, LocalDateTime.now(), 420)
    ));

    public Energy getCurrent() {
        int maxKey = Collections.max(energieByTime.keySet());
        return energieByTime.get(maxKey);
    }

    public Energy getHistoric(int index) {
        return energieByTime.get(index);
    }
}
