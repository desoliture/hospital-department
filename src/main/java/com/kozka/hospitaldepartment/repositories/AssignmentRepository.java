package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Kozka Ivan
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> getAssignmentsByPatientId(Integer index);
    List<Assignment> getAllByPatient(User user);
    List<Assignment> getAllByAssigned(User user);
    List<Assignment> getAllByAssigner(User user);

    @Modifying
    @Query("update Assignment a set a.assigned = ?1, a.assigner =?2," +
            "a.patient = ?3, a.assgType = ?4, a.assignmentDate = ?5," +
            "a.creationDate = ?6, a.description = ?7," +
            "a.completed = ?8, a.conclusion = ?9 where a.id = ?10")
    void setAssignmentDetailsById(
            User assigned, User assigner,
            User patient, AssignmentType assgType,
            LocalDateTime assgDate, LocalDateTime creatDate,
            String descr, Boolean completed,
            String conclusion, Integer id
    );
}
