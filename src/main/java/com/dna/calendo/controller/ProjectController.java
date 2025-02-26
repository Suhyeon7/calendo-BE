package com.dna.calendo.controller;

import com.dna.calendo.config.auth.dto.*;
import com.dna.calendo.domain.project.Project;
import com.dna.calendo.domain.project.ProjectSchedule;
import com.dna.calendo.google.User;
import com.dna.calendo.repository.*;
import com.dna.calendo.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final NotificationRepository notificationRepository;
    private final ProjectScheduleRepository projectScheduleRepository;
    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final ProjectService projectService;

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest projectRequest) {
        // 현재 로그인한 사용자 (SessionUser로 가져오기)
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        // 디버깅 로그 추가
        System.out.println("프로젝트 이름: " + projectRequest.getProjectName());
        System.out.println("초대할 멤버: " + projectRequest.getMembers());

        // DB에서 User 객체 조회 (이메일 기반)
        Optional<User> currentUserOpt = userRepository.findByEmail(sessionUser.getEmail());
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }

        User currentUser = currentUserOpt.get();  // DB에서 가져온 실제 User 객체

        try {
            // 1. 프로젝트 생성
            Project project = new Project();
            project.setProjectName(projectRequest.getProjectName());
            project.setCreatedBy(currentUser.getId());
            projectRepository.save(project);

            System.out.println("프로젝트 생성 완료: " + project.getId());

            // 2. 프로젝트 생성자를 멤버로 추가
            projectMemberRepository.addMember(project.getId(), currentUser.getId());

            // 3. 초대할 멤버들에게 알림 전송
            List<Map<String, Object>> invitations = new ArrayList<>();
            for (String nickname : projectRequest.getMembers()) {
                Optional<User> receiverOpt = userRepository.findByNickName(nickname);

                if (receiverOpt.isPresent()) {
                    User receiver = receiverOpt.get();

                    Notifications notification = new Notifications();
                    notification.setSenderId(currentUser.getId());
                    notification.setReceiverId(receiver.getId());
                    notification.setProjectId(project.getId());
                    notification.setMessage(currentUser.getNickName() + "님이 '" + project.getProjectName() + "' 프로젝트에 " + receiver.getNickName() + "님을 초대하셨습니다.");
                    notificationRepository.save(notification);

                    System.out.println("알림 전송 완료: " + receiver.getNickName());

                    // 응답 데이터에 초대된 사용자 정보 추가
                    invitations.add(Map.of(
                            "nickname", receiver.getNickName(),
                            "email", receiver.getEmail(),
                            "message", notification.getMessage()
                    ));
                } else {
                    System.out.println("초대할 사용자를 찾을 수 없습니다: " + nickname);
                }
            }

            // JSON 형태의 응답 반환
            ResponseEntity<?> response = ResponseEntity.ok(Map.of(
                    "message", "✅ 프로젝트가 성공적으로 생성되고 초대가 전송되었습니다!",
                    "projectId", project.getId(),
                    "projectName", project.getProjectName(),
                    "createdBy", Map.of(
                            "id", currentUser.getId(),
                            "nickname", currentUser.getNickName(),
                            "email", currentUser.getEmail()
                    ),
                    "invitations", invitations
            ));
            System.out.println("✅ 응답 데이터: " + response);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            e.printStackTrace();  // 예외 전체 출력
            return ResponseEntity.status(500).body(Map.of(
                    "error", "서버 오류로 인해 프로젝트 생성에 실패했습니다.",
                    "exception", e.getMessage()
            ));
        }
    }


    // ✅ 프로젝트 조회 기능 추가
    @GetMapping
    public ResponseEntity<List<Project>> getUserProjects() {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (sessionUser == null) {
            return ResponseEntity.status(401).body(null);
        }

        Optional<User> currentUserOpt = userRepository.findByEmail(sessionUser.getEmail());
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User currentUser = currentUserOpt.get();

        List<Project> createdProjects = projectRepository.findByCreatedBy(currentUser.getId());
        List<Project> memberProjects = projectRepository.findProjectsByUserId(currentUser.getId());

        Set<Project> allProjects = new HashSet<>();
        allProjects.addAll(createdProjects);
        allProjects.addAll(memberProjects);

        if (allProjects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new ArrayList<>(allProjects));
    }

    /**특정 프로젝트 조회*/
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable Long projectId) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        return ResponseEntity.ok(project); // ✅ Project 객체 그대로 반환
    }




    /**
     * 프로젝트 멤버 조회
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<User>> getProjectMembers(@PathVariable Long projectId) {
        List<User> members = projectMemberRepository.findMembersByProjectId(projectId);

        if (members.isEmpty()) {
            return ResponseEntity.noContent().build();  // 멤버가 없을 경우 204 No Content
        }

        return ResponseEntity.ok(members);  // 멤버 목록 반환
    }

    /**
     * ✅ 프로젝트 일정 추가 API (응답에 프로젝트 및 일정 정보 포함)
     */
    @Transactional(noRollbackFor = Exception.class)
    @PostMapping("/{projectId}/schedules")
    public ResponseEntity<?> addProjectSchedule(@PathVariable Long projectId, @RequestBody ProjectScheduleRequest request) {
        // 현재 로그인한 사용자 확인
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        System.out.println("📌 프로젝트 ID: " + projectId);
        System.out.println("📌 요청 데이터: " + request);

        // 프로젝트 존재 여부 확인
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "프로젝트를 찾을 수 없습니다."));
        }

        Project project = projectOpt.get();

        // 현재 사용자가 해당 프로젝트의 멤버인지 확인
        Optional<User> currentUserOpt = userRepository.findByEmail(sessionUser.getEmail());
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }

        User currentUser = currentUserOpt.get();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            return ResponseEntity.status(403).body(Map.of("error", "이 프로젝트에 대한 권한이 없습니다."));
        }

        try {
            request.validate();  // ✅ 유효성 검사 추가

            // 프로젝트 일정 생성 및 저장
            ProjectSchedule schedule = new ProjectSchedule();
            schedule.setProjectId(projectId);
            schedule.setTitle(request.getTitle());
            schedule.setStartDateTime(request.getStartDateTime());
            schedule.setEndDateTime(request.getEndDateTime());

            System.out.println("✅ 일정 저장 전 데이터: " + schedule);
            projectScheduleRepository.save(schedule);
            System.out.println("✅ 일정 저장 완료");

            // ✅ JSON 응답으로 프로젝트 및 일정 정보 반환
            return ResponseEntity.ok(Map.of(
                    "message", "✅ 프로젝트 일정이 성공적으로 추가되었습니다.",
                    "projectId", project.getId(),
                    "projectName", project.getProjectName(),
                    "addedSchedule", Map.of(
                            "scheduleId", schedule.getProjectScheduleId(),
                            "title", schedule.getTitle(),
                            "startDateTime", schedule.getStartDateTime(),
                            "endDateTime", schedule.getEndDateTime()
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "서버 오류로 인해 프로젝트 일정 추가에 실패했습니다.",
                    "exception", e.getMessage()
            ));
        }
    }


    // ✅ 특정 프로젝트의 일정 목록 조회
    @GetMapping("/{projectId}/schedules")
    public ResponseEntity<?> getProjectSchedules(@PathVariable Long projectId) {
        List<ProjectSchedule> schedules = projectScheduleRepository.findByProjectId(projectId);

        if (schedules.isEmpty()) {
            return ResponseEntity.status(204).body("해당 프로젝트에 일정이 없습니다.");
        }

        return ResponseEntity.ok(schedules);
    }

    /** ✅ 특정 일정 조회 API 추가 */
    @GetMapping("/{projectId}/schedules/{scheduleId}")
    public ResponseEntity<?> getProjectScheduleById(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId) {

        Optional<ProjectSchedule> scheduleOpt = projectScheduleRepository.findByProjectIdAndProjectScheduleId(projectId, scheduleId);

        if (scheduleOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 해당 일정을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(scheduleOpt.get());
    }


    /** ✅ 특정 일정 수정 (PUT) */
    @PutMapping("/{projectId}/schedules/{scheduleId}")
    public ResponseEntity<?> updateProjectSchedule(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId,
            @RequestBody Map<String, String> updatedScheduleData) {

        Optional<ProjectSchedule> existingScheduleOpt = projectScheduleRepository.findByProjectIdAndProjectScheduleId(projectId, scheduleId);

        if (existingScheduleOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "❌ 해당 일정이 존재하지 않거나 프로젝트 ID가 일치하지 않습니다."
            ));
        }

        ProjectSchedule existingSchedule = existingScheduleOpt.get();

        // 새 값 설정 (값이 존재할 경우만 업데이트)
        String newTitle = updatedScheduleData.get("title");
        String newStartDateTimeStr = updatedScheduleData.get("startDateTime");
        String newEndDateTimeStr = updatedScheduleData.get("endDateTime");

        if (newTitle != null) {
            existingSchedule.setTitle(newTitle);
        }
        if (newStartDateTimeStr != null) {
            existingSchedule.setStartDateTime(LocalDateTime.parse(newStartDateTimeStr));
        }
        if (newEndDateTimeStr != null) {
            existingSchedule.setEndDateTime(LocalDateTime.parse(newEndDateTimeStr));
        }

        projectScheduleRepository.save(existingSchedule);

        // ✅ JSON 응답으로 수정된 일정 정보 반환
        return ResponseEntity.ok(Map.of(
                "message", "✅ 일정이 성공적으로 수정되었습니다.",
                "projectId", projectId,
                "scheduleId", existingSchedule.getProjectScheduleId(),
                "updatedSchedule", Map.of(
                        "title", existingSchedule.getTitle(),
                        "startDateTime", existingSchedule.getStartDateTime(),
                        "endDateTime", existingSchedule.getEndDateTime()
                )
        ));
    }


    /** ✅ 특정 일정 삭제 */
    @DeleteMapping("/{projectId}/schedules/{scheduleId}")
    public ResponseEntity<?> deleteProjectSchedule(
            @PathVariable Long projectId,
            @PathVariable Long scheduleId) {

        Optional<ProjectSchedule> scheduleOpt = projectScheduleRepository.findByProjectIdAndProjectScheduleId(projectId, scheduleId);

        if (scheduleOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 해당 일정이 존재하지 않습니다.");
        }

        projectScheduleRepository.delete(scheduleOpt.get());
        return ResponseEntity.ok(Map.of("message", "✅ 일정이 삭제되었습니다."));
    }


    /** 프로젝트 테마 색상 조회 */
    @GetMapping("/{projectId}/mainTheme")
    public ResponseEntity<?> getProjectThemeColor(@PathVariable Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 프로젝트를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(Map.of("temaColor", projectOpt.get().getTemaColor()));
    }

    /** 프로젝트 테마 색상 변경 */
    @PutMapping("/{projectId}/mainTheme")
    public ResponseEntity<?> updateProjectThemeColor(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> request) {

        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "❌ 프로젝트를 찾을 수 없습니다.",
                    "projectId", projectId
            ));
        }

        Project project = projectOpt.get();
        String newColor = request.get("temaColor");

        if (newColor == null || !newColor.matches("^#[0-9A-Fa-f]{6}$")) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "❌ 올바른 색상 코드가 아닙니다.",
                    "providedColor", newColor
            ));
        }

        // 기존 색상 저장 (변경 전 색상 확인 가능)
        String oldColor = project.getTemaColor();

        // 색상 변경 및 저장
        project.setTemaColor(newColor);
        projectRepository.save(project);

        // JSON 형태의 응답 반환
        return ResponseEntity.ok(Map.of(
                "message", "✅ 테마 색상이 변경되었습니다.",
                "projectId", project.getId(),
                "projectName", project.getProjectName(),
                "oldColor", oldColor,
                "newColor", newColor
        ));
    }




//    /** 타임테이블 페이지 이동 (captain)*/
//    @GetMapping("/captain-timetable")
//    public String getCaptainTimetable(@RequestParam Long projectId) {
//        return "captain-timetable"; // ✅ templates/captain-timetable.html 반환
//    }

//    /**
//     * 멤버 초대 및 추가
//     */
//    @PostMapping("/{projectId}/invite")
//    public ResponseEntity<String> inviteMember(@PathVariable Long projectId, @RequestBody Map<String, String> request) {
//        String nickName = request.get("nickName");
//        Optional<User> receiverOpt = userRepository.findByNickName(nickName);
//
//        if (receiverOpt.isEmpty()) {
//            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
//        }
//
//        User receiver = receiverOpt.get();
//        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
//
//        // 알림 전송
//        Notifications notification = new Notifications();
//        notification.setSenderId(sessionUser.getId());
//        notification.setReceiverId(receiver.getUserId());
//        notification.setProjectId(projectId);
//        notification.setMessage(sessionUser.getNickName() + "님이 '" + projectRepository.findById(projectId).get().getProjectName() + "' 프로젝트에 초대하셨습니다.");
//        notificationRepository.save(notification);
//
//        return ResponseEntity.ok("초대가 전송되었습니다.");
//    }
//
//
//    @PostMapping("/{projectId}/accept-invite")
//    public ResponseEntity<String> acceptInvite(@PathVariable Long projectId) {
//        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
//
//        if (sessionUser == null) {
//            return ResponseEntity.status(401).body("로그인이 필요합니다.");
//        }
//
//        // 사용자가 이미 프로젝트 멤버인지 확인
//        boolean isAlreadyMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, sessionUser.getId());
//        if (isAlreadyMember) {
//            return ResponseEntity.status(400).body("이미 이 프로젝트의 멤버입니다.");
//        }
//
//        // 사용자를 프로젝트 멤버로 추가
//        projectMemberRepository.addMember(projectId, sessionUser.getId());
//        return ResponseEntity.ok("프로젝트에 성공적으로 추가되었습니다.");
//    }



}
