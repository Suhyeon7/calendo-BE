package com.dna.calendo.service;

import com.dna.calendo.config.auth.dto.SessionUser;
import com.dna.calendo.google.User;
import com.dna.calendo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    /**
     * ✅ 현재 로그인한 사용자의 정보를 세션에서 가져오기
     */
    public Optional<User> getLoggedInUser() {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

        if (sessionUser == null) {
            System.out.println("🚨 세션에서 사용자 정보를 찾을 수 없음");
            return Optional.empty();
        }

        System.out.println("✅ 세션에서 가져온 user_id: " + sessionUser.getId());

        Optional<User> user = userRepository.findById(sessionUser.getId());

        if (user.isEmpty()) {
            System.out.println("🚨 데이터베이스에서 사용자 찾을 수 없음: user_id=" + sessionUser.getId());
        } else {
            System.out.println("✅ 로그인된 사용자 정보: " + user.get().getName() + " (ID: " + user.get().getId() + ")");
        }

        return user;
    }
}
