package com.dna.calendo.repository;

import com.dna.calendo.domain.ProjectMember;
import com.dna.calendo.google.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO project_members (project_id, user_id) VALUES (:projectId, :userId)", nativeQuery = true)
    void addMember(@Param("projectId") Long projectId, @Param("userId") Long userId);


    // ✅ 사용자가 프로젝트의 멤버인지 확인하는 메서드
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    // ✅ 프로젝트 ID로 멤버 조회
    @Query(value = "SELECT u.* FROM users u JOIN project_members pm ON u.user_id = pm.user_id WHERE pm.project_id = :projectId", nativeQuery = true)
    List<User> findMembersByProjectId(@Param("projectId") Long projectId);



}
