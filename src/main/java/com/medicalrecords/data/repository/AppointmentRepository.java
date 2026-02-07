package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPatientIdOrderByAppointmentDateDesc(Long patientId);

    List<Appointment> findAllByDoctorIdOrderByAppointmentDateDesc(Long doctorId);

    List<Appointment> findAllByPatientIdAndAppointmentDateBetweenOrderByAppointmentDateDesc(
            Long patientId, LocalDate from, LocalDate to
    );

    @Query("""
            SELECT doctor.id,
                   doctor.medicalId,
                   doctor.firstName,
                   doctor.lastName,
                   COUNT(appointment)
            FROM Appointment appointment
            JOIN appointment.doctor doctor
            GROUP BY doctor.id,
                     doctor.medicalId,
                     doctor.firstName,
                     doctor.lastName
           """)
    List<Object[]> countAppointmentsPerDoctor();

    @Query("""
            SELECT diagnosis.id,
                   diagnosis.name,
                   COUNT(DISTINCT patient.id)
            FROM Appointment appointment
            JOIN appointment.diagnosis diagnosis
            JOIN appointment.patient patient
            GROUP BY diagnosis.id,
                     diagnosis.name
           """)
    List<Object[]> countDistinctPatientsPerDiagnosis();
}