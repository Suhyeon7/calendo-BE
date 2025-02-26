package com.dna.calendo.service;

import com.dna.calendo.domain.schedule.Schedule;
import com.dna.calendo.config.auth.dto.ScheduleDto;
import com.dna.calendo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  // Spring Bean으로 등록
@RequiredArgsConstructor

public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /** ✅ 사용자 ID를 기준으로 일정 목록 조회 **/
    public List<ScheduleDto> getSchedulesByUserId(Long userId) {
        return scheduleRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /** ✅ 특정 날짜의 일정 조회 (사용자 ID 기반) **/
    public List<ScheduleDto> getSchedulesByDate(Long userId, String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

        return scheduleRepository.findByUserIdAndStartDateTimeBetween(userId, startOfDay, endOfDay)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /** ✅ 일정 추가 (기본값 처리 포함) **/
    public void addSchedule(Long userId, ScheduleDto scheduleDto) {
        if (scheduleDto.getTitle() == null || scheduleDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("일정 제목(title)은 필수 입력값입니다.");
        }

        if (scheduleDto.getStartDateTime() == null || scheduleDto.getEndDateTime() == null) {
            throw new IllegalArgumentException("일정 시작 및 종료 시간은 필수 입력값입니다.");
        }

        System.out.println("✅ 일정 저장 - 사용자 ID: " + userId + ", 제목: " + scheduleDto.getTitle());

        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setTitle(scheduleDto.getTitle());
        schedule.setStartDateTime(scheduleDto.getStartDateTime());
        schedule.setEndDateTime(scheduleDto.getEndDateTime());

        schedule.setRepeatType(
                scheduleDto.getRepeatType() == null || scheduleDto.getRepeatType().isEmpty()
                        ? Schedule.RepeatType.NONE
                        : Schedule.RepeatType.valueOf(scheduleDto.getRepeatType())
        );

        scheduleRepository.save(schedule);
    }

    /** 일정 추가 **/
    @Transactional
    public void updateSchedule(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다. ID: " + id));

        // 일정 제목 업데이트
        if (scheduleDto.getTitle() != null && !scheduleDto.getTitle().isEmpty()) {
            schedule.setTitle(scheduleDto.getTitle());
        }

        // 일정 시작 날짜 업데이트
        if (scheduleDto.getStartDateTime() != null) {
            schedule.setStartDateTime(scheduleDto.getStartDateTime());
        }

        // 일정 종료 날짜 업데이트
        if (scheduleDto.getEndDateTime() != null) {
            schedule.setEndDateTime(scheduleDto.getEndDateTime());
        }

        // 반복 타입 업데이트
        String repeatType = scheduleDto.getRepeatType();
        if (repeatType == null || repeatType.isEmpty()) {
            schedule.setRepeatType(Schedule.RepeatType.NONE);  // 기본값 설정
        } else {
            try {
                schedule.setRepeatType(Schedule.RepeatType.valueOf(repeatType));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 반복 유형입니다: " + repeatType);
            }
        }

        scheduleRepository.save(schedule);
    }



    /** ✅ 일정 삭제 (존재 여부 확인 후 삭제) **/
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 일정이 존재하지 않습니다. ID: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    /** 🔹 일정 DTO 변환 메서드 **/
    private ScheduleDto convertToDto(Schedule schedule) {
        return new ScheduleDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getStartDateTime(),
                schedule.getEndDateTime(),
                schedule.getRepeatType().name(),
                schedule.getUserId()
        );
    }

    /** 🔹 일정 데이터 유효성 검사 **/
    private void validateSchedule(ScheduleDto scheduleDto) {
        if (scheduleDto.getTitle() == null || scheduleDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("일정 제목(title)은 필수 입력값입니다.");
        }
        if (scheduleDto.getStartDateTime() == null || scheduleDto.getEndDateTime() == null) {
            throw new IllegalArgumentException("일정 시작 및 종료 시간은 필수 입력값입니다.");
        }
    }
}
