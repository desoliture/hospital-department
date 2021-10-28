package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Kozka Ivan
 */
@Repository
public interface AssignmentRepository extends PagingAndSortingRepository<Assignment, Integer> {
    List<Assignment> getAssignmentsByPatientId(Integer index);
    List<Assignment> getAllByPatient(User user);
    List<Assignment> getAllByAssigned(User user);
    List<Assignment> getAllByAssigner(User user);

    @Query("select a from Assignment a")
    List<Assignment> getAll();

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
