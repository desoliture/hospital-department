package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.TreeSet;

/**
 * @author Kozka Ivan
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    TreeSet<Assignment> getAssignmentsByPatientId(Integer index);
}
