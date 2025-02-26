package com.dna.calendo.google;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "Admin"),    //관리자
    USER("ROLE_USER", "User");     //사용자

    private final String key;
    private final String title;
}