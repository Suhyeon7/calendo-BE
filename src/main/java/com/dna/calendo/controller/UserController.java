package com.dna.calendo.controller;

import com.dna.calendo.google.User;
import com.dna.calendo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * ✅ 마이페이지 - 현재 로그인된 사용자 정보 조회
     */
    @GetMapping("/mypage")
    public String getMypage(Model model, HttpSession session) {
        Optional<User> userOpt = userService.getLoggedInUser();

        if (userOpt.isEmpty()) {
            System.out.println("🚨 마이페이지 요청 시 사용자 정보 없음 (로그인 필요)");
            return "redirect:/";  // 로그인되지 않은 경우 홈으로 이동
        }

        User user = userOpt.get();
        model.addAttribute("user", user);

        System.out.println("✅ 마이페이지 접속 - 사용자 ID: " + user.getId());

        return "mypage";  // mypage.html 반환
    }

    /**
     * ✅ API: 현재 로그인한 사용자 정보 조회 (JSON 응답)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(HttpSession session) {
        Optional<User> userOpt = userService.getLoggedInUser();

        if (userOpt.isEmpty()) {
            System.out.println("🚨 사용자 정보 없음 (세션 만료 또는 로그인 필요)");
            return ResponseEntity.status(401).body("{\"message\": \"로그인이 필요합니다.\"}");
        }

        User user = userOpt.get();
        System.out.println("✅ 사용자 정보 조회 성공 - ID: " + user.getId());

        return ResponseEntity.ok(user);
    }

}