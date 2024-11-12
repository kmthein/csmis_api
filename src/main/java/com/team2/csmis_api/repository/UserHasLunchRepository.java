package com.team2.csmis_api.repository;


import com.team2.csmis_api.dto.LunchSummaryDTO;
import com.team2.csmis_api.entity.UserHasLunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserHasLunchRepository extends JpaRepository<UserHasLunch, Integer> {
    @Query(value = "SELECT " +
            "SUM(register_and_eat) AS registerAndEat, " +
            "SUM(register_not_eat) AS registerNotEat, " +
            "SUM(unregister_but_eat) AS unregisterButEat " +
            "FROM ( " +
            "    SELECT " +
            "        SUM(CASE WHEN d.user_id IS NOT NULL THEN 1 ELSE 0 END) AS register_and_eat, " +
            "        SUM(CASE WHEN d.user_id IS NULL THEN 1 ELSE 0 END) AS register_not_eat, " +
            "        0 AS unregister_but_eat " +
            "    FROM user_has_lunch u " +
            "    LEFT JOIN door_access_record d " +
            "        ON u.user_id = d.user_id AND DATE(u.dt) = DATE(d.date) " +
            "    WHERE DATE(u.dt) BETWEEN :startDate AND :endDate " +  // Monthly range filter
            "    UNION ALL " +
            "    SELECT " +
            "        0 AS register_and_eat, " +
            "        0 AS register_not_eat, " +
            "        SUM(CASE WHEN u.user_id IS NULL THEN 1 ELSE 0 END) AS unregister_but_eat " +
            "    FROM door_access_record d " +
            "    LEFT JOIN user_has_lunch u " +
            "        ON d.user_id = u.user_id AND DATE(d.date) = DATE(u.dt) " +
            "    WHERE u.user_id IS NULL " +
            "    AND DATE(d.date) BETWEEN :startDate AND :endDate " +  // Monthly range filter
            ") AS combined",
            nativeQuery = true)
    LunchSummaryDTO getLunchSummaryBetweenTwo(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "SELECT " +
            "SUM(register_and_eat) AS registerAndEat, " +
            "SUM(register_not_eat) AS registerNotEat, " +
            "SUM(unregister_but_eat) AS unregisterButEat " +
            "FROM ( " +
            "    SELECT " +
            "        SUM(CASE WHEN d.user_id IS NOT NULL THEN 1 ELSE 0 END) AS register_and_eat, " +
            "        SUM(CASE WHEN d.user_id IS NULL THEN 1 ELSE 0 END) AS register_not_eat, " +
            "        0 AS unregister_but_eat " +
            "    FROM user_has_lunch u " +
            "    LEFT JOIN door_access_record d " +
            "        ON u.user_id = d.user_id AND DATE(u.dt) = DATE(d.date) " +
            "    WHERE DATE(u.dt) = :targetDate " +  // Filter for the specific day
            "    UNION ALL " +
            "    SELECT " +
            "        0 AS register_and_eat, " +
            "        0 AS register_not_eat, " +
            "        SUM(CASE WHEN u.user_id IS NULL THEN 1 ELSE 0 END) AS unregister_but_eat " +
            "    FROM door_access_record d " +
            "    LEFT JOIN user_has_lunch u " +
            "        ON d.user_id = u.user_id AND DATE(d.date) = DATE(u.dt) " +
            "    WHERE u.user_id IS NULL " +
            "    AND DATE(d.date) = :targetDate " +  // Filter for the specific day
            ") AS combined",
            nativeQuery = true)
    LunchSummaryDTO getDailyData(@Param("targetDate") String targetDate);

    List<UserHasLunch> findByUserId(Integer userId);

    @Query("SELECT u.dt FROM UserHasLunch u WHERE u.user.id = :userId")
    List<Date> findDtByUserId(@Param("userId") int userId);

    Optional<UserHasLunch> findByUserIdAndDt(Long userId, Date dt);

    @Query("SELECT uhl FROM UserHasLunch uhl WHERE DATE(uhl.dt) = CURDATE()")
    List<UserHasLunch> findByCurrentDate();
}
