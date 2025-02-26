package com.dna.calendo.config.auth.dto;

import lombok.Getter;
import com.dna.calendo.google.User;
import java.io.Serializable;

@Getter
public class SessionUser implements Serializable { // 직렬화 기능을 가진 세션 DTO

    private Long id;
    private String name;
    private String email;
    private String picture;
    private String nickName;
    private String temaColor;

    // 기존 생성자 (User 객체 전체를 받는 경우)
    public SessionUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
        this.nickName = user.getNickName() != null ? user.getNickName() : extractNickNameFromEmail(user.getEmail());
        this.temaColor = user.getTemaColor();
    }

    // 새롭게 추가할 생성자 (name, email, picture만 받는 경우)
    public SessionUser(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.nickName = extractNickNameFromEmail(email); // 이메일에서 닉네임 추출
    }

    public SessionUser(String name, String email, String picture, String temaColor) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.nickName = extractNickNameFromEmail(email); // 이메일에서 닉네임 추출
        this.temaColor = temaColor;  // 테마 색상 추가
    }


    // 이메일에서 @ 앞부분 추출하는 메서드
    private String extractNickNameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return "userrrr";  // 예외 처리: 이메일이 없거나 형식이 맞지 않는 경우 기본 닉네임
    }
}
