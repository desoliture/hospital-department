package com.kozka.hospitaldepartment.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Kozka Ivan
 */
@Entity
@Table(name = "assignments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Assignment implements Comparable<Assignment> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "assg_id")
    Integer id;

    @OneToOne
    @JoinColumn(name = "pat_id")
    User patient;

    @OneToOne
    @JoinColumn(name = "assigner_id")
    User assigner;

    @OneToOne
    @JoinColumn(name = "assigned_id")
    User assigned;

    @Column(name = "assg_type")
    @Enumerated(value = EnumType.STRING)
    AssignmentType assgType;

    @Column(name = "descr")
    String description;

    @Column
    Boolean completed;

    @Column(name = "assg_date")
    Date assignmentDate;

    @Column
    String conclusion;

    public Assignment(User patient, User assigner,
                      User assigned, AssignmentType assgType,
                      String description, Boolean completed,
                      Date assignmentDate) {
        this.patient = patient;
        this.assigner = assigner;
        this.assigned = assigned;
        this.assgType = assgType;
        this.description = description;
        this.completed = completed;
        this.assignmentDate = assignmentDate;
    }

    @Override
    public int compareTo(Assignment other) {
        return this.assignmentDate
                .compareTo(other.assignmentDate);
    }
}
