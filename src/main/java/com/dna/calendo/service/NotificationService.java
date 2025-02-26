package com.dna.calendo.service;

import com.dna.calendo.config.auth.dto.Notifications;
import com.dna.calendo.repository.NotificationRepository;
import com.dna.calendo.repository.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    // 사용자 ID로 알림 조회
    public List<Notifications> getNotificationsForUser(Long recipientId) {
        return notificationRepository.findByReceiverId(recipientId);
    }

    // 초대 수락/거절 처리
    public void respondToInvitation(Long notificationId, boolean isAccepted) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        if (isAccepted) {
            projectMemberRepository.addMember(notification.getProjectId(), notification.getReceiverId());
        }

        notification.setStatus(isAccepted ? "ACCEPTED" : "DECLINED");
        notificationRepository.save(notification);
    }
}
