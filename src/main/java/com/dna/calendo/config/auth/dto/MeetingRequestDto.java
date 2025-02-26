package com.dna.calendo.config.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingRequestDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime deadline;
}
