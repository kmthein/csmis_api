package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Feedback;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    Optional<Feedback> findByUserIdAndDate(Integer userId, LocalDate date);

//    @Query("SELECT f FROM Feedback f WHERE f.user.id = :userId AND f.date = :date")
//    List<Feedback> findByUserIdAndDate(Integer userId, LocalDate date);

    @Query(value = "SELECT COUNT(*) FROM feedback WHERE feedback_response_id = :responseId", nativeQuery = true)
    long countFeedbacksByResponseId(@Param("responseId") Integer responseId);

    boolean existsByUserIdAndLunchId(Long userId, Long lunchId);

    @Query("SELECT f FROM Feedback f " +
            "LEFT JOIN FETCH f.user u " +
            "LEFT JOIN FETCH f.response r " +
            "LEFT JOIN FETCH f.lunch l " +
            "WHERE f.isDeleted = false")
    List<Feedback> findAllActiveFeedbacksWithDetails();

    // Fetch all feedbacks where isDeleted is false
    @Query("SELECT f FROM Feedback f WHERE f.isDeleted = false")
    List<Feedback> findAllActiveFeedbacks();

    // Fetch feedback by ID if it is not deleted
    @Query("SELECT f FROM Feedback f WHERE f.id = :id AND f.isDeleted = false")
    Optional<Feedback> findActiveFeedbackById(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE Feedback f SET f.isDeleted=true WHERE f.id=?1")
    void deleteFeedback(Integer id);


}
