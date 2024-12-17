package com.example.clinic.appointment.service;

import com.example.clinic.appointment.dto.AppointmentCreationDTO;
import com.example.clinic.appointment.entity.Appointment;
import com.example.clinic.appointment.entity.AppointmentsType;
import com.example.clinic.appointment.mapper.AppointmentMapper;
import com.example.clinic.appointment.repository.AppointmentRepository;
import com.example.clinic.appointment.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentsTypeService appointmentsTypeService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Appointment createAppointment(AppointmentCreationDTO appointmentDto) {
        AppointmentsType appointmentsType = appointmentsTypeService
                .getAppointmentsType(appointmentDto.getAppointmentTypeId());

        Appointment appointment = appointmentMapper.appointmentDtoToEntity(appointmentDto);
    
        appointment.setPatient(appointmentDto.getPatientId());
        appointment.setAppointmentType(appointmentsType);

        if (testForAppointmentCollision(appointmentsType.getDoctor(), appointment)) {
            throw new IllegalArgumentException("Appointment collision");
        }
        
        return appointmentRepository.save(appointment);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Appointment updateAppointment(Long id, AppointmentCreationDTO appointmentDto) {
        Appointment appointment = this.getAppointmentById(id);

        appointment.setAppointmentDate(appointmentDto.getAppointmentDate());
        appointment.setPatient(appointmentDto.getPatientId());
        appointment.setAppointmentType(appointmentsTypeService.getAppointmentsType(appointmentDto.getAppointmentTypeId()));

        if (testForAppointmentCollision(appointment.getAppointmentType().getDoctor(), appointment)) {
            throw new IllegalArgumentException("Appointment collision");
        }

        return appointmentRepository.save(appointment);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Appointment with id " + id + " not found");
        }
        appointmentRepository.deleteById(id);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment with id " + id + " not found"));
    }

    public List<Appointment> getAppointmentsByDoctorIdAndTimeInterval(Long doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDoctorIdAndTimeInterval(doctorId, start, end);
    }

    public Page<Appointment> getAppointments(Pageable page) {
        return appointmentRepository.findAll(page);
    }


    public boolean testForAppointmentCollision(Long doctorId, Appointment appointment) {
        LocalDateTime start = appointment.getAppointmentDate();
        LocalDateTime end = appointment.getAppointmentEnd();

        List<Appointment> collisionCandidates = appointmentRepository.findByDoctorIdAndTimeInterval(doctorId, start.toLocalDate().atStartOfDay(), start.plusDays(1).toLocalDate().atStartOfDay());
        collisionCandidates
                .removeIf(a -> (a.getAppointmentDate().isBefore(start) && a.getAppointmentEnd().isBefore(start)));
        collisionCandidates
                .removeIf(a -> (a.getAppointmentDate().isAfter(end) && a.getAppointmentEnd().isAfter(end)));
        collisionCandidates
                .removeIf(a -> a.getId().equals(appointment.getId()));
        return !collisionCandidates.isEmpty();
    }
}
