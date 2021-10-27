package com.kozka.hospitaldepartment.entities;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    String email;

    @Column(nullable = false)
    String pass;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "u_role", nullable = false)
    @Enumerated(value = EnumType.STRING)
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
}
