package com.dna.calendo.repository;

import com.dna.calendo.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByProjectId(Long projectId); // 올바른 메서드 명
}

