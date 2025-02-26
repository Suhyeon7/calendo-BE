package com.dna.calendo.domain.project;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter  // Lombok 어노테이션으로 getter 자동 생성
@Setter  // Lombok 어노테이션으로 setter 자동 생성
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;  // 필드를 id로 설정

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "tema_color", length = 7, nullable = false)
    private String temaColor = "#FFFFFF";  // 기본 색상: 흰색

    // ✅ equals와 hashCode 추가
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getter/Setter
    public Long getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }


}
