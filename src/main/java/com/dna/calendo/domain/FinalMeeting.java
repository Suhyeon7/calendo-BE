package com.dna.calendo.domain;

import com.dna.calendo.domain.project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "final_meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinalMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private LocalDate confirmedDate;

    @Column(nullable = false)
    private LocalTime confirmedStartTime;

    @Column(nullable = false)
    private LocalTime confirmedEndTime;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
