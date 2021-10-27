package com.kozka.hospitaldepartment.entities;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Kozka Ivan
 */
@Entity
@Table(name = "assignments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Assignment implements Comparable<Assignment> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assg_id")
    Integer id;

    @OneToOne
    @JoinColumn(name = "pat_id", nullable = false)
    User patient;

    @OneToOne
    @JoinColumn(name = "assigner_id", nullable = false)
    User assigner;

    @OneToOne
    @JoinColumn(name = "assigned_id", nullable = false)
    User assigned;

    @Column(name = "assg_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    AssignmentType assgType;

    @Column(name = "descr", nullable = false)
    String description;

    @Column(nullable = false)
    Boolean completed;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "assg_date", nullable = false)
    LocalDateTime assignmentDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @Column
    String conclusion;

    public Assignment(User patient, User assigner,
                      User assigned, AssignmentType assgType,
                      String description, Boolean completed,
                      LocalDateTime assignmentDate,
                      LocalDateTime creationDate) {
        this.patient = patient;
        this.assigner = assigner;
        this.assigned = assigned;
        this.assgType = assgType;
        this.description = description;
        this.completed = completed;
        this.assignmentDate = assignmentDate;
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(Assignment other) {
        return this.assignmentDate
                .compareTo(other.assignmentDate);
    }
}
