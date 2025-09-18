package com.AdisApplication.AdisApplication.Repository;


import com.AdisApplication.AdisApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(String role);

    @Modifying
    @Query("UPDATE User u SET u.sessionExpiry = :expiry WHERE u.id = :userId")
    void updateSessionExpiry(@Param("userId") Long userId, @Param("expiry") LocalDateTime expiry);

    @Query("SELECT u FROM User u WHERE u.sessionExpiry > :now")
    List<User> findActiveSessions(@Param("now") LocalDateTime now);
}