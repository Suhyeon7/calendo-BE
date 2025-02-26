package com.dna.calendo.repository;

import com.dna.calendo.config.auth.dto.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    //List<Notifications> findByReceiverId(Long receiverId);

    @Query("SELECT n FROM Notifications n WHERE n.receiverId = :receiverId ORDER BY n.createdAt DESC")
    List<Notifications> findByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findUserIdByEmail(String email);
}