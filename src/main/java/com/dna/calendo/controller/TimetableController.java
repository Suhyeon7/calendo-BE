package com.dna.calendo.controller;


import org.springframework.ui.Model;
import com.dna.calendo.domain.Timetable;
import com.dna.calendo.domain.project.Project;
import com.dna.calendo.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/api/timetables")
public class TimetableController {
    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // ✅ 회의 일정 생성 (캡틴 전용)
    @PostMapping("/{projectId}/create")
    public ResponseEntity<Timetable> createTimetable(
            @PathVariable Long projectId,
            @RequestBody Timetable timetable
    ) {
        timetable.setProjectId(projectId);
        return ResponseEntity.ok(timetableService.createTimetable(timetable));
    }

    // ✅ 특정 프로젝트의 회의 일정 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<List<Timetable>> getProjectTimetables(@PathVariable Long projectId) {
        return ResponseEntity.ok(timetableService.getTimetablesByProjectId(projectId));
    }

    @GetMapping("/captain-timetable")
    public String getCaptainTimetable(@RequestParam Long projectId, Model model) {
        model.addAttribute("projectId", projectId); // 프로젝트 ID 전달
        return "captain-timetable"; // ✅ templates/captain-timetable.html 반환
    }
}
