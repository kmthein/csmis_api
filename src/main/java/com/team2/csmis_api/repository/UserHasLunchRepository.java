package com.team2.csmis_api.repository;


import com.team2.csmis_api.dto.AvoidMeatDTO;
import com.team2.csmis_api.dto.DateCountDTO;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.dto.LunchSummaryDTO;
import com.team2.csmis_api.entity.UserHasLunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserHasLunchRepository extends JpaRepository<UserHasLunch, Integer> {

    @Query("SELECT SUM(ul.userCost) FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE FUNCTION('YEARWEEK', d.date) = FUNCTION('YEARWEEK', CURRENT_DATE) " +
            "AND d.user.id = :userId")
    BigDecimal getWeeklyTotalUserCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(ul.userCost) FROM UserHasLunch ul " +
            "LEFT JOIN DoorAccessRecord d ON ul.user.id = d.user.id " +
            "AND FUNCTION('DATE', ul.dt) = FUNCTION('DATE', d.date) " +
            "WHERE FUNCTION('MONTH', ul.dt) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', ul.dt) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND ul.user.id = :userId")
    BigDecimal getMonthlyTotalUserCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(ul.userCost) FROM UserHasLunch ul " +
            "LEFT JOIN DoorAccessRecord d ON ul.user.id = d.user.id " +
            "AND FUNCTION('DATE', ul.dt) = FUNCTION('DATE', d.date) " +
            "WHERE FUNCTION('YEAR', ul.dt) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND ul.user.id = :userId")
    BigDecimal getYearlyTotalUserCostByUserId(@Param("userId") Integer userId);


    @Query("SELECT new com.team2.csmis_api.dto.DateCountDTO(u.dt, COUNT(u), u.total_cost) " +
            "FROM UserHasLunch u " +
            "WHERE u.dt BETWEEN :startDate AND :endDate " +
            "GROUP BY u.dt, u.total_cost " +
            "ORDER BY u.dt")
    List<DateCountDTO> getLunchCounts(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = """
        SELECT 
            d.name AS departmentName, 
            :start AS start, 
            :end AS end, 
            SUM(l.price) AS totalCost 
        FROM 
            lunch l
        INNER JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        INNER JOIN 
            user u ON uhl.user_id = u.id
        INNER JOIN 
            department d ON u.department_id = d.id
        WHERE 
            l.date BETWEEN :start AND :end
        GROUP BY 
            d.name
        ORDER BY 
            d.name
    """, nativeQuery = true)
    List<Object[]> getLunchCostBetweenTwoDate(@Param("start") String start, @Param("end") String end);

    @Query(value = """
        SELECT 
            d.name AS departmentName, 
            DATE_FORMAT(l.date, '%Y-%m-%d') AS date, 
            SUM(l.price) AS totalCost 
        FROM 
            lunch l
        INNER JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        INNER JOIN 
            user u ON uhl.user_id = u.id
        INNER JOIN 
            department d ON u.department_id = d.id
        WHERE 
            DATE_FORMAT(l.date, '%Y-%m-%d') = :date
        GROUP BY 
            d.name, DATE_FORMAT(l.date, '%Y-%m-%d')
        ORDER BY 
            d.name
    """, nativeQuery = true)
    List<Object[]> getLunchCostByDay(@Param("date") String date);

    @Query(value = """
        SELECT 
            d.name AS departmentName, 
            DATE_FORMAT(l.date, '%Y') AS year, 
            SUM(l.price) AS totalCost 
        FROM 
            lunch l
        INNER JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        INNER JOIN 
            user u ON uhl.user_id = u.id
        INNER JOIN 
            department d ON u.department_id = d.id
        WHERE 
            DATE_FORMAT(l.date, '%Y') = :year
        GROUP BY 
            d.name, DATE_FORMAT(l.date, '%Y')
        ORDER BY 
            d.name
    """, nativeQuery = true)
    List<Object[]> getLunchCostByYearly(@Param("year") String year);

    @Query(value = """
        SELECT 
            d.name AS departmentName, 
            DATE_FORMAT(l.date, '%m') AS month, 
            DATE_FORMAT(l.date, '%Y') AS year, 
            SUM(l.price) AS totalCost 
        FROM 
            lunch l
        INNER JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        INNER JOIN 
            user u ON uhl.user_id = u.id
        INNER JOIN 
            department d ON u.department_id = d.id
        WHERE 
            DATE_FORMAT(l.date, '%Y-%m') = CONCAT(:year, '-', :month)
        GROUP BY 
            d.name, DATE_FORMAT(l.date, '%m'), DATE_FORMAT(l.date, '%Y')
        ORDER BY 
            d.name
    """, nativeQuery = true)
    List<Object[]> getLunchCostByYearAndMonth(@Param("month") String month, @Param("year") String year);

    @Query(value = """
        SELECT 
            d.name AS departmentName, 
            DATE_FORMAT(l.date, '%m') AS month, 
            SUM(l.price) AS totalCost 
        FROM 
            lunch l
        INNER JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        INNER JOIN 
            user u ON uhl.user_id = u.id
        INNER JOIN 
            department d ON u.department_id = d.id
        WHERE 
            DATE_FORMAT(l.date, '%m') = :selectedMonth
        GROUP BY 
            d.name, DATE_FORMAT(l.date, '%m')
        ORDER BY 
            d.name
    """, nativeQuery = true)
    List<Object[]> getMonthlyLunchCostByDepartment(@Param("selectedMonth") String selectedMonth);

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
            "SUM(combined.register_and_eat) AS registerAndEat, " +
            "SUM(combined.register_not_eat) AS registerNotEat, " +
            "SUM(combined.unregister_but_eat) AS unregisterButEat " +
            "FROM (" +
            "    SELECT " +
            "        SUM(CASE WHEN d.user_id IS NOT NULL THEN 1 ELSE 0 END) AS register_and_eat, " +
            "        SUM(CASE WHEN d.user_id IS NULL THEN 1 ELSE 0 END) AS register_not_eat, " +
            "        0 AS unregister_but_eat " +
            "    FROM user_has_lunch u " +
            "    LEFT JOIN door_access_record d " +
            "        ON u.user_id = d.user_id AND DATE(u.dt) = DATE(d.date) " +
            "    WHERE MONTH(u.dt) = :month " +
            "    AND YEAR(u.dt) = :year " +
            "    UNION ALL " +
            "    SELECT " +
            "        0 AS register_and_eat, " +
            "        0 AS register_not_eat, " +
            "        SUM(CASE WHEN u.user_id IS NULL THEN 1 ELSE 0 END) AS unregister_but_eat " +
            "    FROM door_access_record d " +
            "    LEFT JOIN user_has_lunch u " +
            "        ON d.user_id = u.user_id AND DATE(d.date) = DATE(u.dt) " +
            "    WHERE u.user_id IS NULL " +
            "    AND MONTH(d.date) = :month " +
            "    AND YEAR(d.date) = :year " +
            ") AS combined", nativeQuery = true)
    LunchSummaryDTO lunchSummaryByMonthYear(@Param("month") String month, @Param("year") String year);

    @Query(value = "SELECT " +
            "SUM(combined.register_and_eat) AS registerAndEat, " +
            "SUM(combined.register_not_eat) AS registerNotEat, " +
            "SUM(combined.unregister_but_eat) AS unregisterButEat " +
            "FROM (" +
            "    SELECT " +
            "        SUM(CASE WHEN d.user_id IS NOT NULL THEN 1 ELSE 0 END) AS register_and_eat, " +
            "        SUM(CASE WHEN d.user_id IS NULL THEN 1 ELSE 0 END) AS register_not_eat, " +
            "        0 AS unregister_but_eat " +
            "    FROM user_has_lunch u " +
            "    LEFT JOIN door_access_record d " +
            "        ON u.user_id = d.user_id AND DATE(u.dt) = DATE(d.date) " +
            "    WHERE YEAR(u.dt) = :year " +
            "    UNION ALL " +
            "    SELECT " +
            "        0 AS register_and_eat, " +
            "        0 AS register_not_eat, " +
            "        SUM(CASE WHEN u.user_id IS NULL THEN 1 ELSE 0 END) AS unregister_but_eat " +
            "    FROM door_access_record d " +
            "    LEFT JOIN user_has_lunch u " +
            "        ON d.user_id = u.user_id AND DATE(d.date) = DATE(u.dt) " +
            "    WHERE u.user_id IS NULL " +
            "    AND YEAR(d.date) = :year " +
            ") AS combined", nativeQuery = true)
    LunchSummaryDTO lunchSummaryByYear(@Param("year") String year);

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

    @Query("SELECT u FROM UserHasLunch u WHERE u.dt = :date")
    List<UserHasLunch> findByDate(Date date);
    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND FUNCTION('DATE', u.dt) = :date")
    List<UserHasLunch> findRegisteredNotEatDaily(@Param("date") LocalDate date);

    @Query("SELECT COUNT(u) FROM UserHasLunch u WHERE u.user.id = :userId AND MONTH(u.dt) = MONTH(CURRENT_DATE) AND YEAR(u.dt) = YEAR(CURRENT_DATE)")
    int countRegisteredDaysForMonth(@Param("userId") Integer userId);

    @Query("SELECT u FROM UserHasLunch u WHERE MONTH(u.dt) = :month AND YEAR(u.dt) = :year")
    List<UserHasLunch> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT u FROM UserHasLunch u WHERE u.dt BETWEEN :startOfWeek AND :endOfWeek")
    List<UserHasLunch> findByDtBetween(@Param("startOfWeek") Date startOfWeek, @Param("endOfWeek") Date endOfWeek);

    @Query("SELECT u FROM UserHasLunch u WHERE u.dt BETWEEN :startOfPreviousWeek AND :endOfPreviousWeek")
    List<UserHasLunch> findUserHasLunchForPreviousWeek(
            @Param("startOfPreviousWeek") Date startOfPreviousWeek,
            @Param("endOfPreviousWeek") Date endOfPreviousWeek
    );

    @Query("SELECT uhl FROM UserHasLunch uhl WHERE MONTH(uhl.dt) = :month AND YEAR(uhl.dt) = :year")
    List<UserHasLunch> findUserHasLunchForMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT uhl FROM UserHasLunch uhl JOIN uhl.user u WHERE uhl.dt BETWEEN :startOfWeek AND :endOfWeek AND u.department.id = :departmentId")
    List<UserHasLunch> findUserHasLunchForPreviousWeekByDepartment(
            @Param("startOfWeek") Date startOfWeek,
            @Param("endOfWeek") Date endOfWeek,
            @Param("departmentId") Integer departmentId
    );

    @Query("SELECT uhl FROM UserHasLunch uhl JOIN uhl.user u WHERE MONTH(uhl.dt) = :month AND YEAR(uhl.dt) = :year AND u.department.id = :departmentId")
    List<UserHasLunch> findUserHasLunchForMonthAndDepartment(@Param("month") int month, @Param("year") int year, @Param("departmentId") int departmentId);

    @Query("SELECT u FROM UserHasLunch u WHERE YEAR(u.dt) = :year")
    List<UserHasLunch> findUserHasLunchForYear(@Param("year") int year);

    @Query("SELECT uhl FROM UserHasLunch uhl JOIN uhl.user u WHERE YEAR(uhl.dt) = :year AND u.department.id = :departmentId")
    List<UserHasLunch> findUserHasLunchForYearAndDepartment(@Param("year") int year, @Param("departmentId") int departmentId);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND CAST(u.dt AS LocalDate) BETWEEN :startDate AND :endDate")
    List<UserHasLunch> findRegisteredNotEatWeekly(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND FUNCTION('MONTH', u.dt) = :month " +
            "AND FUNCTION('YEAR', u.dt) = :year")
    List<UserHasLunch> findRegisteredNotEatMonthly(@Param("month") int month, @Param("year") int year);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND FUNCTION('YEAR', u.dt) = :year")
    List<UserHasLunch> findRegisteredNotEatYearly(@Param("year") int year);

    @Query("SELECT uhl FROM UserHasLunch uhl WHERE DATE(uhl.dt) = CURDATE()")
    List<UserHasLunch> findByCurrentDate();

    @Query("SELECT COUNT(u) FROM UserHasLunch u WHERE u.dt = :date")
    long countByDate(@Param("date") Date date);

    @Query(value = """
        SELECT\s
            CASE\s
                WHEN m.id IS NULL THEN 'is_vegan' \s
                ELSE m.name                      \s
            END AS meat,
            DATE_FORMAT(dates.dt, '%d-%m-%Y (%a)') AS day, \s
            COUNT(
                CASE\s
                    WHEN m.id IS NULL THEN \s
                        CASE\s
                            WHEN u.is_vegan = 1 AND uhl.user_id = u.id THEN u.id \s
                            ELSE NULL
                        END
                    ELSE \s
                        CASE\s
                            WHEN uam.user_id IS NOT NULL THEN uam.user_id
                            ELSE NULL
                        END
                END
            ) AS count
        FROM\s
            (SELECT NULL AS id, 'is_vegan' AS name \s
             UNION ALL
             SELECT id, name FROM meat) m
        CROSS JOIN\s
            (SELECT DISTINCT uhl.dt\s
             FROM user_has_lunch uhl
             WHERE uhl.dt >= DATE_ADD(CURRENT_DATE(), INTERVAL (7 - WEEKDAY(CURRENT_DATE())) DAY)
               AND uhl.dt < DATE_ADD(CURRENT_DATE(), INTERVAL (14 - WEEKDAY(CURRENT_DATE())) DAY)
               AND WEEKDAY(uhl.dt) < 5) dates 
        LEFT JOIN\s
            user_has_lunch uhl ON uhl.dt = dates.dt
        LEFT JOIN\s
            user u ON uhl.user_id = u.id \s
        LEFT JOIN\s
            user_avoid_meat uam ON uhl.user_id = uam.user_id AND uam.meat_id = m.id
        GROUP BY\s
            meat, dates.dt \s
        ORDER BY\s
            dates.dt, meat;
       """, nativeQuery = true)
    List<Object[]> getUserAvoidMeatForNextWeek();

    @Query("SELECT u.total_cost FROM UserHasLunch u WHERE u.dt = :date")
    Double getTotalByDate(@Param("date") Date date);

    @Query("SELECT COUNT(u) FROM UserHasLunch u WHERE u.user.id = :userId AND MONTH(u.dt) = MONTH(CURRENT_DATE) AND YEAR(u.dt) = YEAR(CURRENT_DATE)")
    int countRegisterDaysForMonth(@Param("userId") Integer userId);
    @Query("SELECT COUNT(u.dt) FROM UserHasLunch u WHERE u.user.id = :userId AND MONTH(u.dt) = :monthValue AND YEAR(u.dt) = :year")
    List<LocalDate> findRegisterDatesByUserIdAndMonth(@Param("userId") Integer userId, @Param("monthValue") int monthValue, @Param("year") int year);

    @Query("SELECT COUNT(u.dt) FROM UserHasLunch u WHERE u.user.id = :userId AND MONTH(u.dt) = :monthValue AND YEAR(u.dt) = :year")
    int countRegisteredDatesForMonth(Integer userId, int monthValue, int year);
    @Query("SELECT COUNT(u.dt) FROM UserHasLunch u " +
            "WHERE u.user.id = :userId " +
            "AND u.dt >= :startOfMonth " +
            "AND u.dt < :endOfMonth")
    int countRegisterDatesForMonth(@Param("userId") Integer userId,
                                   @Param("startOfMonth") Date startOfMonth,
                                   @Param("endOfMonth") Date endOfMonth);

}
