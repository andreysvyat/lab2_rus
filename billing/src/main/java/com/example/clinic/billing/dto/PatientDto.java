package com.example.clinic.billing.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PatientDto(
    Long id,
    String name,
    LocalDate dateOfBirth,
    String email,
    List<AppointmentDto> appointments
) {}