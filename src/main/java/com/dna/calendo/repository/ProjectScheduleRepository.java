package com.dna.calendo.repository;

import com.dna.calendo.domain.project.ProjectSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectScheduleRepository extends JpaRepository<ProjectSchedule, Long> {
    // ✅ 특정 프로젝트의 일정 조회 (불필요한 데이터 로딩 방지)
    @Query("SELECT ps FROM ProjectSchedule ps WHERE ps.projectId = :projectId")
    List<ProjectSchedule> findByProjectId(Long projectId);

    // ✅ 특정 프로젝트의 특정 일정 조회 (올바른 ID 사용)
    @Query("SELECT ps FROM ProjectSchedule ps WHERE ps.projectId = :projectId AND ps.projectScheduleId = :scheduleId")
    Optional<ProjectSchedule> findByProjectIdAndProjectScheduleId(
            @Param("projectId") Long projectId,
            @Param("scheduleId") Long scheduleId
    );

}



