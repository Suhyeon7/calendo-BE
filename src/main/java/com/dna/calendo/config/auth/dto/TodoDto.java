package com.dna.calendo.config.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    private Long id;         // 투두 ID
    private String title;    // 투두 제목
    private boolean checked; // 체크 여부
    private Long userId;     // 사용자 ID
}
