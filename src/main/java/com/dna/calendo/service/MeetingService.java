package com.dna.calendo.service;

import com.dna.calendo.domain.Meeting;
import com.dna.calendo.config.auth.dto.MeetingRequestDto;
import com.dna.calendo.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;

    @Transactional
    public Meeting createMeeting(Long projectId, MeetingRequestDto requestDto) {
        Meeting meeting = new Meeting(
                projectId,
                requestDto.getTitle(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getDeadline()
        );
        return meetingRepository.save(meeting);
    }
}
