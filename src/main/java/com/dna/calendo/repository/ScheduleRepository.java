package com.dna.calendo.repository;

import com.dna.calendo.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /** ✅ 특정 사용자의 전체 일정 목록 조회 **/
    List<Schedule> findByUserId(Long userId);

    /** ✅ 특정 사용자의 특정 날짜 일정 조회 **/
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND s.startDateTime >= :start AND s.startDateTime < :end")
    List<Schedule> findByUserIdAndStartDateTimeBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
