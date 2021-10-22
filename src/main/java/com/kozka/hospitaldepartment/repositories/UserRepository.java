package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Kozka Ivan
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByEmail(String email);
    User getUserById(Integer index);
    List<User> getUsersByUserRole(UserRole role);
    List<User> getUsersByUserRoleAndActiveIsTrue(UserRole role);
    List<User> getUsersByUserRoleAndActiveIsFalse(UserRole role);
    List<User> getUsersByActiveTrue();
}
