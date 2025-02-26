package com.dna.calendo.google;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")  // 테이블 이름 명시
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")  // DB에서는 user_id로 저장
    private Long id;  // 필드명은 id로 변경 (SessionUser와 일관성 유지)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name="nickName", nullable = false, unique = true)
    private String nickName;

    @Column(name = "temaColor", length = 7)
    private String temaColor;

    // ✅ getUserId 메서드
    public Long getUserId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();  // ROLE_USER, ROLE_ADMIN 반환
    }
}
