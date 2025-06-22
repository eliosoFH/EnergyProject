package org.example.usageservice.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.usageservice.dto.EnergyUsageHourly;
import org.example.usageservice.repository.EnergyUsageDatabaseRepository;
import org.example.usageservice.repository.EnergyUsageHourlyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
        import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

    @Mock
    private EnergyUsageDatabaseRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessageListener messageListener;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private EnergyUsageHourly producerData;
    private EnergyUsageHourly userData;

    @BeforeEach
    void setup() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 10, 0);

        producerData = new EnergyUsageHourly();
        producerData.setTimestamp(timestamp);
        producerData.setCommunityProduced(100.0);

        userData = new EnergyUsageHourly();
        userData.setTimestamp(timestamp.plusHours(1)); // Stunde geändert → löst dbAndRabbit() aus
        userData.setCommunityUsed(150.0);
    }

    @Test
    void loadContext() throws Exception {

        messageListener.receiveProducer(mapper.writeValueAsString(producerData));
        messageListener.receiveUser(mapper.writeValueAsString(userData));

        EnergyUsageHourly producerDataNextHour = new EnergyUsageHourly();
        producerDataNextHour.setTimestamp(userData.getTimestamp());
        producerDataNextHour.setCommunityProduced(50.0);
        messageListener.receiveProducer(mapper.writeValueAsString(producerDataNextHour));

        verify(repository, times(1)).save(any(EnergyUsageHourlyEntity.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("update"), anyString());
    }
}