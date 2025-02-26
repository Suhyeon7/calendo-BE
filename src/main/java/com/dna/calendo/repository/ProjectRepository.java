package com.dna.calendo.repository;

import com.dna.calendo.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 사용자가 생성한 프로젝트 조회
    List<Project> findByCreatedBy(Long userId);

    // 사용자가 멤버로 속한 프로젝트 조회 (프로젝트 멤버 테이블과 조인)
    @Query("SELECT p FROM Project p JOIN ProjectMember pm ON p.id = pm.projectId WHERE pm.userId = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);

    // ✅ 멤버 추가 메서드
    @Query(value = "INSERT INTO project_members (project_id, user_id) VALUES (:projectId, :userId)", nativeQuery = true)
    void addMember(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // ✅ 멤버 중복 여부 확인 메서드
    @Query("SELECT COUNT(pm) > 0 FROM ProjectMember pm WHERE pm.projectId = :projectId AND pm.userId = :userId")
    boolean existsByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

}
