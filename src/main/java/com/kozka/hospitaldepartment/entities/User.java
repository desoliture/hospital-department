package com.kozka.hospitaldepartment.entities;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
* @author Kozka Ivan
*/
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Please enter the email")
    String email;

    @Column(nullable = false)
    @NotBlank(message = "Password can`t be empty")
    String pass;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name can`t be empty")
    String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name can`t be empty")
    String lastName;

    @Column(name = "u_role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Please chose the user role")
    UserRole userRole;

    @Column(name = "spec")
    @Enumerated(value = EnumType.STRING)
    Specialization specialization;

    @Column
    Boolean active;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birth;

    public User(String email, String pass,
                String firstName, String lastName,
                UserRole userRole, Specialization specialization,
                Boolean active, LocalDate birth) {
        this.email = email;
        this.pass = pass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
        this.specialization = specialization;
        this.active = active;
        this.birth = birth;
    }

    public String toStringLight() {
        return firstName + " " + lastName + " | " + email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(userRole);
    }

    @Override
    public String getPassword() {
        return pass;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
