package com.example.demo.repository;

import com.example.demo.entity.MeetingPoll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingPollRepository extends JpaRepository<MeetingPoll, Long> {

    Optional<MeetingPoll> findById(int id);
    //TODO: mb need sql query to get all poll by user email
    List<MeetingPoll> findByUserEmail(String email);
    Optional<MeetingPoll> findByTitleAndId(String title, Long id);

}
