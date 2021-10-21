package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.InactiveCause;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Kozka Ivan
 */
public interface CauseRepository extends JpaRepository<InactiveCause, Integer> {
}
