package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Kozka Ivan
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> getAssignmentsByPatientId(Integer index);
    List<Assignment> getAllByPatient(User user);
}
