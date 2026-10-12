package com.serenityspa.repository;

import com.serenityspa.entity.Appointment;
import com.serenityspa.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(Long customerId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.treatment.id = :treatmentId AND a.appointmentDate = :date AND a.startTime = :time AND a.status <> :cancelledStatus")
    boolean existsConflictingAppointment(
            @Param("treatmentId") Long treatmentId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    List<Appointment> findByTreatmentIdAndAppointmentDateAndStartTimeAndStatusNot(
            Long treatmentId, LocalDate date, LocalTime startTime, AppointmentStatus status);

    List<Appointment> findByStatusOrderByAppointmentDateAscStartTimeAsc(AppointmentStatus status);
}
