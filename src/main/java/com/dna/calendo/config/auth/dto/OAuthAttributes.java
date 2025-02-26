package com.dna.calendo.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import com.dna.calendo.google.Role;
import com.dna.calendo.google.User;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;  // OAuth2User에서 받은 전체 사용자 정보
    private String nameAttributeKey;         // OAuth2 인증 시 식별자로 사용하는 key
    private String name;                     // 사용자 이름
    private String email;                    // 사용자 이메일
    private String picture;                  // 사용자 프로필 사진 URL

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey, String name,
                           String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // OAuth2User에서 반환하는 사용자 정보는 Map
    // 따라서 값 하나하나를 변환해야 한다.
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        System.out.println("OAuthAttributes.of() called with attributes: " + attributes);  // 디버깅 로그 추가

        return ofGoogle(userNameAttributeName, attributes);
    }

    // 구글 생성자
    private static OAuthAttributes ofGoogle(String usernameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    // User 엔티티 생성
    public User toEntity() {
        String nickName = extractNickNameFromEmail(this.email);  // 이메일에서 닉네임 추출
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .nickName(nickName)
                .build();
    }
    // 이메일에서 @ 앞부분을 닉네임으로 추출하는 메서드
    private String extractNickNameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return "userrrr";  // 이메일 형식이 잘못된 경우 기본 닉네임 설정
    }

}