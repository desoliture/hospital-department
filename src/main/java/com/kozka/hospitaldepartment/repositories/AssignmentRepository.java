package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Kozka Ivan
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

}
