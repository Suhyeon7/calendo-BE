package com.dna.calendo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime deadline;

    public Meeting(Long projectId, String title, LocalDate startDate, LocalDate endDate, LocalDateTime deadline) {
        this.projectId = projectId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deadline = deadline;
    }
}
