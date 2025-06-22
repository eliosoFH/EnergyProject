package com.example.BackEndAPI;

import com.example.BackEndAPI.Controller.EnergyController;
import com.example.BackEndAPI.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EnergyControllerTest {

    @Mock
    private CurrentPercentageDatabaseRepository currentRepo;

    @Mock
    private EnergyUsageDatabaseRepository energyRepo;

    @InjectMocks
    private EnergyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new EnergyController(currentRepo, energyRepo);
    }

    @Test
    void testGetCurrentHour_returnsDataIfExists() {
        // Arrange
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        CurrentPercentageEntity mockEntity = new CurrentPercentageEntity();
        when(currentRepo.findById(now)).thenReturn(Optional.of(mockEntity));

        // Act
        CurrentPercentageEntity result = controller.getCurrentHour();

        // Assert
        assertThat(result).isNotNull();
        verify(currentRepo).findById(now);
    }

    @Test
    void testGetHistoricData_filtersAndSorts() {
        // Arrange
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hour1 = now.minusHours(2);
        LocalDateTime hour2 = now.minusHours(1);

        EnergyUsageHourlyEntity e1 = new EnergyUsageHourlyEntity();
        e1.setHour(hour1);
        EnergyUsageHourlyEntity e2 = new EnergyUsageHourlyEntity();
        e2.setHour(hour2);
        EnergyUsageHourlyEntity e3 = new EnergyUsageHourlyEntity();
        e3.setHour(now);

        when(energyRepo.findAll()).thenReturn(List.of(e1, e2, e3));

        // Act
        List<EnergyUsageHourlyEntity> result = controller.getHistoricData(hour1, now);

        // Assert
        assertThat(result).containsExactly(e1, e2, e3);
        verify(energyRepo).findAll();
    }
}
