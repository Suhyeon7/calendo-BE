package com.dna.calendo.domain.schedule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(nullable = false, name = "end_date_time")
    private LocalDateTime endDateTime;


    @Column(name = "repeat_type")
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    public enum RepeatType {
        NONE, WEEKLY, MONTHLY, YEARLY
    }
}
