package com.example.demo.repository;

import com.example.demo.entity.MeetingPoll;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingPollRepository extends JpaRepository<MeetingPoll, Long> {
    List<MeetingPoll> findAllByUser(User user);
}
