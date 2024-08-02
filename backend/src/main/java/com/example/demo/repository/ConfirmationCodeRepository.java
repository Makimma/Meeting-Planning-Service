package com.example.demo.repository;

import com.example.demo.entity.ConfirmationCode;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
    Optional<ConfirmationCode> findByCode(String token);
    Optional<ConfirmationCode> findFirstByUserOrderByIdDesc(User user);
}
