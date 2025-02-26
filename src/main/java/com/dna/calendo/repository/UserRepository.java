package com.dna.calendo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dna.calendo.google.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByNickName(String nickName);
    Optional<User> findByNickName(String NickName);
    // 닉네임으로 사용자 검색 (부분 일치)
    List<User> findByNickNameContainingIgnoreCase(String nickName);
}