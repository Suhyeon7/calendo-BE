package com.dna.calendo.controller;


import com.dna.calendo.config.auth.dto.MeetingRequestDto;
import com.dna.calendo.domain.Meeting;
import com.dna.calendo.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/meetings")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@PathVariable Long projectId, @RequestBody MeetingRequestDto requestDto) {
        Meeting meeting = meetingService.createMeeting(projectId, requestDto);
        return ResponseEntity.ok(meeting);
    }
}
