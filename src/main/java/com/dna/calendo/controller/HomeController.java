package com.dna.calendo.controller;

import com.dna.calendo.config.auth.dto.*;
import com.dna.calendo.domain.ProjectMember;
import com.dna.calendo.domain.project.Project;
import com.dna.calendo.config.auth.dto.OAuthAttributes;
import com.dna.calendo.config.auth.dto.ScheduleDto;
import com.dna.calendo.config.auth.dto.SessionUser;
import com.dna.calendo.google.User;
import com.dna.calendo.repository.NotificationRepository;
import com.dna.calendo.repository.ProjectMemberRepository;
import com.dna.calendo.repository.ProjectRepository;
import com.dna.calendo.repository.UserRepository;
import com.dna.calendo.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@CrossOrigin("*")
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;
    private final ScheduleService scheduleService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @GetMapping("/createProject")
    public String createProjectPage() {
        return "createProject";  // src/main/resources/templates/createProject.html 파일을 반환
    }
    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notification";  // src/main/resources/templates/notification.html 파일 반환
    }

    @GetMapping("/project")
    public String projectPage(@RequestParam(value = "projectId", required = false) Long projectId,
                              @RequestParam(value = "projectName", required = false) String projectName,
                              Model model) {
        if (projectId != null && projectName != null) {
            model.addAttribute("projectId", projectId);
            model.addAttribute("projectName", projectName);
        } else {
            model.addAttribute("errorMessage", "프로젝트 정보가 없습니다.");
        }
        return "project";
    }



    /**
     * 메인 페이지 - OAuth 로그인 및 일정 불러오기
     */
    @GetMapping("/")
    public String home(Model model, OAuth2AuthenticationToken authentication) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (authentication != null) {
            String registrationId = authentication.getAuthorizedClientRegistrationId();
            String userNameAttributeName = authentication.getName();
            Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

            OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);
            User user = oAuthAttributes.toEntity();

            // ✅ 닉네임이 없으면 이메일을 닉네임으로 설정
            if (user.getNickName() == null || user.getNickName().isEmpty()) {
                user.setNickName(user.getEmail());
            }

            // ✅ DB에서 사용자 조회, 없으면 저장
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isEmpty()) {
                User savedUser = userRepository.save(user);
                sessionUser = new SessionUser(savedUser);
            } else {
                User dbUser = existingUser.get();
                sessionUser = new SessionUser(dbUser);
            }

            // ✅ 세션에 사용자 저장
            httpSession.setAttribute("user", sessionUser);
        }

        // ✅ 사용자 정보가 있을 경우 화면에 전달
        if (sessionUser != null) {
            model.addAttribute("userName", sessionUser.getName() != null ? sessionUser.getName() : "사용자");
            model.addAttribute("temaColor", sessionUser.getTemaColor());
            model.addAttribute("email", sessionUser.getEmail());
            model.addAttribute("profileImage", sessionUser.getPicture());

            // ✅ 사용자의 일정 목록 가져오기
            Optional<User> userOptional = userRepository.findByEmail(sessionUser.getEmail());
            if (userOptional.isPresent()) {
                Long userId = userOptional.get().getId();
                List<ScheduleDto> schedules = scheduleService.getSchedulesByUserId(userId);
                model.addAttribute("schedules", schedules);
            }
        } else {
            model.addAttribute("userName", null);  // 로그아웃 상태로 설정
        }

        return "list";
    }

    /**
     * ✅ 일정 추가
     */
    @PostMapping("/add-schedule")
    public String addSchedule(@ModelAttribute ScheduleDto scheduleDto) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user == null) {
            return "redirect:/"; // 로그인되지 않은 경우 홈으로 리다이렉트
        }

        if (scheduleDto.getTitle() == null || scheduleDto.getTitle().isEmpty()) {
            return "redirect:/";
        }

        scheduleDto.setUserId(user.getId()); // ✅ 사용자 ID 설정
        scheduleService.addSchedule(user.getId(), scheduleDto);
        return "redirect:/";
    }

    /**
     * ✅ 일정 삭제
     */
    @GetMapping("/delete-schedule/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/";
    }

    /**
     * ✅ 일정 수정
     */
    @PutMapping("/api/schedules/update/{id}")
    public ResponseEntity<String> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDto scheduleDto) {
        scheduleService.updateSchedule(id, scheduleDto);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }

    /**
     * ✅ 테마 색상 변경
     */
//    @PostMapping("/change-theme")
//    @ResponseBody
//    public ResponseEntity<String> changeTheme(@RequestParam String color) {
//        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
//
//        if (sessionUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
//        }
//
//        Optional<User> optionalUser = userRepository.findByEmail(sessionUser.getEmail());
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            user.setTemaColor(color);
//            userRepository.save(user);
//
//            // ✅ 세션 정보 업데이트
//            httpSession.setAttribute("user", new SessionUser(user));
//
//            return ResponseEntity.ok("테마 색상이 변경되었습니다!");
//        }
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다.");
//    }
    @PostMapping("/change-theme")
    @ResponseBody
    public ResponseEntity<Map<String, String>> changeTheme(@RequestParam String color) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (sessionUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "로그인이 필요합니다."));
        }

        Optional<User> optionalUser = userRepository.findByEmail(sessionUser.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setTemaColor(color);
            userRepository.save(user);

            // 세션 정보 업데이트
            httpSession.setAttribute("user", new SessionUser(user));

            Map<String, String> response = new HashMap<>();
            response.put("temaColor", color);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "사용자 정보를 찾을 수 없습니다."));
    }


    /**
     * ✅ 특정 사용자의 특정 날짜 일정 조회 API
     */
    @GetMapping("/api/schedules")
    @ResponseBody
    public List<ScheduleDto> getSchedulesByDate(@RequestParam String date) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        return scheduleService.getSchedulesByDate(user.getId(), date);
    }

    /**
     * ✅ 닉네임 사용자 검색 API
     */
    @RestController
    @RequestMapping("/users")
    @RequiredArgsConstructor
    public class UserController {

        private final UserRepository userRepository;

        @GetMapping("/search")
        public List<User> searchUsers(@RequestParam String nickName) {
            return userRepository.findByNickNameContainingIgnoreCase(nickName);
        }
    }
}
