package com.dna.calendo.controller;

import com.dna.calendo.config.auth.dto.Notifications;
import com.dna.calendo.config.auth.dto.ProjectRequest;
import com.dna.calendo.config.auth.dto.SessionUser;
import com.dna.calendo.google.User;
import com.dna.calendo.repository.NotificationRepository;
import com.dna.calendo.repository.ProjectMemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final HttpSession httpSession;

    // 알림 목록 가져오기
    @GetMapping
    public ResponseEntity<?> getNotifications() {
        //User currentUser = (User) httpSession.getAttribute("user");
        SessionUser currentUser = (SessionUser) httpSession.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        List<Notifications> notifications = notificationRepository.findByReceiverId(currentUser.getId());


        return ResponseEntity.ok(notifications);
    }

    // 초대 수락/거절 처리
    @PutMapping("/{notificationId}/respond")
    public ResponseEntity<String> respondToInvitation(@PathVariable Long notificationId, @RequestBody ProjectRequest responseRequest) {
        Optional<Notifications> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isEmpty()) {
            return ResponseEntity.status(404).body("알림을 찾을 수 없습니다.");
        }

        Notifications notification = notificationOpt.get();
        if (!"PENDING".equals(notification.getStatus())) {
            return ResponseEntity.status(400).body("이미 처리된 초대입니다.");
        }

        if (responseRequest.isAccepted()) {  // 수정된 부분
            projectMemberRepository.addMember(notification.getProjectId(), notification.getReceiverId());
            notification.setStatus("ACCEPTED");
            notificationRepository.save(notification);

            return ResponseEntity.ok("초대를 수락했습니다.");
        } else {
            notification.setStatus("DECLINED");
            notificationRepository.save(notification);
            return ResponseEntity.ok("초대를 거절했습니다.");
        }
    }
}
