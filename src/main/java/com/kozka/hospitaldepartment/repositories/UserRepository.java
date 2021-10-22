package com.kozka.hospitaldepartment.repositories;

import com.kozka.hospitaldepartment.entities.Specialization;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    @Modifying
    @Query("update User u set u.firstName = ?1, u.lastName = ?2," +
            "u.email = ?3, u.pass = ?4," +
            "u.userRole = ?5, u.specialization = ?6," +
            "u.active = ?7 where u.id = ?8")
    void setUserDetailsById(String firstName, String lastName,
                            String email, String pass,
                            UserRole userRole, Specialization spec,
                            Boolean active, Integer id
                            );
}
