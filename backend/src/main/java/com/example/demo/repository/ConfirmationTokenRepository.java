package com.example.demo.repository;

import com.example.demo.entity.ConfirmationToken;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
    Optional<ConfirmationToken> findFirstByUserOrderByIdDesc(User user);

//    @Transactional
//    @Modifying
//    @Query("UPDATE ConfirmationToken c SET c.confirmedAt = ?2 " +
//            "WHERE c.token = ?1")
//    int updateConfirmedAt(String token, Date date);
//
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM ConfirmationToken c " +
//            "WHERE c.user.id = ?1")
//    int deleteByUserId(Long userId);
}
