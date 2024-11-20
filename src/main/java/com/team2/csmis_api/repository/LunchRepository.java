package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.UserHasLunch;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LunchRepository extends JpaRepository<Lunch, Integer> {
    @Query("SELECT l FROM Lunch l WHERE l.isDeleted = false ORDER BY l.date DESC")
    public List<Lunch> findAll();

    @Query("SELECT l FROM Lunch l WHERE DATE(l.date) = CURDATE()")
    Lunch findLunchByCurrentDate();

    Optional<Lunch> findByDate(LocalDate date);

    @Query(value = "SELECT * FROM lunch l WHERE " +
            "WEEKDAY(CURDATE()) BETWEEN 0 AND 4 " +
            "AND l.date BETWEEN " +
            "DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) " +
            "AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY) " +
            "OR (" +
            "WEEKDAY(CURDATE()) IN (5, 6) " +
            "AND l.date BETWEEN " +
            "DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 7 DAY) " +
            "AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 13 DAY)" +
            ")", nativeQuery = true)
    public List<Lunch> getCurrentWeekMenu();

    @Modifying
    @Transactional
    @Query("UPDATE Lunch l SET l.isDeleted=true WHERE l.id=?1")
    public void deleteLunch(Integer id);
}