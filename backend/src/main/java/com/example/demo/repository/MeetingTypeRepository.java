package com.example.demo.repository;

import com.example.demo.entity.MeetingType;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingTypeRepository extends JpaRepository<MeetingType, Long> {
    List<MeetingType> findAllByUser(User user);
}
