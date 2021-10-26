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

    /*
    select a.* from assignments as a
    join users u on u.user_id = a.pat_id
    where a.completed = true and u.user_id = 6
    order by a.assg_date
     */
    @Query("select a from Assignment a join User u on a.patient.id = u.id " +
            "where a.completed = true and u.id =?1 order by a.assignmentDate")
    List<Assignment> getHealthCardForUserById(Integer id);

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
