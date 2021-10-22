package com.kozka.hospitaldepartment.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Integer id;

    @Column
    String email;

    @Column
    String pass;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "u_role")
    @Enumerated(value = EnumType.STRING)
    UserRole userRole;

    @Column(name = "spec")
    @Enumerated(value = EnumType.STRING)
    Specialization specialization;

    @Column
    Boolean active;

    @Column
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
}
