package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Feedback;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("SELECT f FROM Feedback f WHERE f.isDeleted = false")
    public List<Feedback> getAllFeedbacks();

    @Modifying
    @Transactional
    @Query("UPDATE Feedback f SET f.isDeleted=true WHERE f.id=?1")
    public void deleteFeedback(Integer id);
}
