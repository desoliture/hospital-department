package com.kozka.hospitaldepartment.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Kozka Ivan
 */
@Entity
@Table(name = "inactive_causes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class InactiveCause {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cause_id")
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "dis_date")
    LocalDateTime disableDate;

    @Column(name = "descr")
    String description;

    public InactiveCause(User user, LocalDateTime disableDate, String description) {
        this.user = user;
        this.disableDate = disableDate;
        this.description = description;
    }
}
