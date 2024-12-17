package com.example.clinic.billing.service;

import com.example.clinic.billing.dto.AppointmentDto;
import com.example.clinic.billing.dto.ConsultationDTO;
import com.example.clinic.billing.dto.InvoiceDTO;
import com.example.clinic.billing.dto.PatientDto;
import com.example.clinic.billing.integration.PatientService;
import com.example.clinic.billing.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final PatientService patientService;

    public InvoiceDTO generateInvoice(List<Long> patientIds) {

        Set<Long> patientIdSet = new HashSet<>(patientIds);

        Set<PatientDto> patients = patientService.getPatientsWithAppointmentsByIds(patientIdSet);
        if(patients.isEmpty()){
            throw new EntityNotFoundException("No patients found for the provided IDs");
        }


        patientIdSet.removeAll(patients.stream().map(PatientDto::id).collect(Collectors.toSet()));

        if (!patientIdSet.isEmpty()) {
            throw new EntityNotFoundException("Patients with ids " + StringUtils.join(patientIdSet, ',') +  " not found.");
        }

        List<AppointmentDto> appointments = patients.stream().flatMap((patient) -> patient.appointments().stream()).collect(Collectors.toList());

        return appointmentListToInvoice(appointments);
    }

    private InvoiceDTO appointmentListToInvoice(List<AppointmentDto> appointments) {
        List<ConsultationDTO> patientConsultations = appointmentToConsultationDTO(appointments);

        BigDecimal totalCost = patientConsultations.stream()
                .map(ConsultationDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return InvoiceDTO.builder()
                .consultations(patientConsultations)
                .totalCost(totalCost)
                .build();
    }


    private ConsultationDTO appointmentToConsultationDTO(AppointmentDto appointment) {
        if (appointment == null) {
            return null;
        }

        ConsultationDTO consultationDTO = new ConsultationDTO();

        consultationDTO.setPrice(appointment.getAppointmentType().price());
        consultationDTO.setDoctor(appointment.getAppointmentType().doctor());

        return consultationDTO;
    }


    private List<ConsultationDTO> appointmentToConsultationDTO(List<AppointmentDto> appointments) {
        if (appointments == null) {
            return null;
        }
        return appointments.stream().map(this::appointmentToConsultationDTO).collect(Collectors.toList());
    }
}
