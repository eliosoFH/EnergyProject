package org.example.usageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentPercentageDatabaseRepository extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {
}
