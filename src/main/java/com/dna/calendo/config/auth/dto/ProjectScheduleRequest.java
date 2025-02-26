package com.dna.calendo.config.auth.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectScheduleRequest {
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public void validate() {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("일정 제목은 필수 입력값입니다.");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("시작 날짜 및 시간은 필수 입력값입니다.");
        }
        if (endDateTime == null) {
            throw new IllegalArgumentException("종료 날짜 및 시간은 필수 입력값입니다.");
        }
    }

    @Override
    public String toString() {
        return "ProjectScheduleRequest{" +
                "title='" + title + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }
}
