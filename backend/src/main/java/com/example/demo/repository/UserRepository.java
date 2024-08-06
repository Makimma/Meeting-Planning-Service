package com.example.demo.repository;

import com.example.demo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByLink(String link);

    Optional<User> findByEmailAndEnabledIsTrue(String email);

    boolean existsByEmailAndEnabledIsTrue(String email);

    boolean existsByLink(String link);

    //FIXME убрать явный sql
    @Transactional
    @Modifying
    @Query("DELETE FROM User a WHERE a.id = ?1")
    void deleteById(Long id);
}
