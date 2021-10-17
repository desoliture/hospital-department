package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Kozka Ivan
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByEmail(String email);
    User getUserById(Integer index);
}
