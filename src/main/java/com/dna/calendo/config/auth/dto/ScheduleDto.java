package com.dna.calendo.config.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private LocalDateTime startDateTime;

    @NonNull
    private LocalDateTime endDateTime;

    @NonNull
    private String repeatType;

    private Long userId;  // ✅ 추가
}
