package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LunchRepository extends JpaRepository<Lunch, Integer> {
    @Query("SELECT l FROM Lunch l WHERE l.isDeleted = false ORDER BY l.updatedAt DESC")
    public List<Lunch> findAll();

    @Query("SELECT l FROM Lunch l WHERE DATE(l.date) = CURDATE()")
    Lunch findLunchByCurrentDate();

    @Query("SELECT l.price FROM Lunch l WHERE l.date = :date")
    Optional<Double> findPriceByDate(@Param("date") LocalDate date);


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

    @Query(value = "SELECT * FROM lunch l WHERE " +
            "l.date BETWEEN " +
            "DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 7 DAY) " +
            "AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 13 DAY)",
            nativeQuery = true)
    public List<Lunch> getNextWeekMenu();

    @Modifying
    @Transactional
    @Query("UPDATE Lunch l SET l.isDeleted=true WHERE l.id=:id")
    public void deleteLunch(Integer id);
    @Query("SELECT l.price FROM Lunch l WHERE l.date = :date")
    Double getPriceByDate(LocalDate date);
//    Optional<Object> findByDtAndLunch(Date lunchDate, String lunchType);

    @Query(value = """
        SELECT 
            l.date AS date,
            o.quantity AS quantity,
            (COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) AS user_id,
            l.price AS price,
            l.company_rate AS company_rate,
            ((o.quantity * l.price) - ((COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) * (l.price - ((l.company_rate / 100 ) * l.price)))) AS amount,
            SUM(
              ((o.quantity * l.price) - ((COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) * (l.price - ((l.company_rate / 100 ) * l.price))))
            ) OVER () AS total_amount
        FROM 
            lunch l
        LEFT JOIN 
            order_row o ON l.date = o.lunch_date
        LEFT JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        LEFT JOIN 
            door_access_record d 
            ON l.date = d.date AND d.user_id NOT IN (
                SELECT user_id FROM user_has_lunch WHERE dt = l.date
            )
        WHERE 
            l.date = :date
        GROUP BY 
            l.date, o.quantity, l.company_rate, l.price
        ORDER BY 
            l.date
        """, nativeQuery = true)
    List<Object[]> getDailyCompanyCosting(@Param("date") LocalDate date);

    // Weekly Report Query
    @Query(value = """
        SELECT 
            l.date AS date,
            o.quantity AS quantity,
            (COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) AS user_id,
            l.price AS price,
            l.company_rate AS company_rate,
            ((o.quantity * l.price) - ((COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) * (l.price - ((l.company_rate / 100 ) * l.price)))) AS amount,
            SUM(
              ((o.quantity * l.price) - ((COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) * (l.price - ((l.company_rate / 100 ) * l.price))))
            ) OVER () AS total_amount
        FROM 
            lunch l
        LEFT JOIN 
            order_row o ON l.date = o.lunch_date
        LEFT JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        LEFT JOIN 
            door_access_record d 
            ON l.date = d.date AND d.user_id NOT IN (
                SELECT user_id FROM user_has_lunch WHERE dt = l.date
            )
        WHERE 
            l.date BETWEEN :startDate AND :endDate
        GROUP BY 
            l.date, o.quantity, l.company_rate, l.price
        ORDER BY 
            l.date
        """, nativeQuery = true)
    List<Object[]> getWeeklyCompanyCosting(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Monthly Report Query
    @Query(value = """
        SELECT 
            l.date AS date,
            o.quantity AS quantity,
            (COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) AS user_id,
            l.price AS price,
            l.company_rate AS company_rate,
            ((o.quantity * l.price) - ((COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) * (l.price - ((l.company_rate / 100 ) * l.price)))) AS amount,
            SUM(
              ((o.quantity * l.price) - ((COUNT(DISTINCT uhl.user_id) + 
            COUNT(DISTINCT d.user_id)) * (l.price - ((l.company_rate / 100 ) * l.price))))
            ) OVER () AS total_amount
        FROM 
            lunch l
        LEFT JOIN 
            order_row o ON l.date = o.lunch_date
        LEFT JOIN 
            user_has_lunch uhl ON l.date = uhl.dt
        LEFT JOIN 
            door_access_record d 
            ON l.date = d.date AND d.user_id NOT IN (
                SELECT user_id FROM user_has_lunch WHERE dt = l.date
            )
        WHERE 
            MONTH(l.date) = :month
            AND YEAR(l.date) = :year
        GROUP BY 
            l.date, o.quantity, l.company_rate, l.price
        ORDER BY 
            l.date
        """, nativeQuery = true)
    List<Object[]> getMonthlyCompanyCosting(@Param("month") int month, @Param("year") int year);


    @Query(nativeQuery = true, value = """
        WITH calculated_data AS (
            SELECT 
                u.name AS name, 
                u.staff_id AS staff_id, 
                SUM(l.price - (l.company_rate / 100) * l.price) AS amount
            FROM (
                SELECT dar.user_id AS user_id, dar.date AS date 
                FROM door_access_record dar
                WHERE dar.date = :date 
                AND NOT EXISTS (
                    SELECT user_id  
                    FROM user_has_lunch uhl
                    WHERE dar.user_id = uhl.user_id 
                    AND dar.date = uhl.dt
                )
                UNION ALL
                SELECT uhl.user_id AS user_id, uhl.dt AS date 
                FROM user_has_lunch uhl
                WHERE uhl.dt = :date 
            ) combined
            JOIN user u ON combined.user_id = u.id
            JOIN lunch l ON combined.date = l.date
            GROUP BY u.name, u.staff_id
        )
        SELECT 
            name, 
            staff_id, 
            amount, 
            SUM(amount) OVER () AS total_amount
        FROM calculated_data
        ORDER BY staff_id
        """)
    List<Object[]> getDailyEmployeeOwnCost(@Param("date") LocalDate date);


    @Query(nativeQuery = true, value = """
        WITH calculated_data AS (
            SELECT 
                u.name AS name, 
                u.staff_id AS staff_id, 
                SUM(l.price - (l.company_rate / 100) * l.price) AS amount
            FROM (
                SELECT dar.user_id AS user_id, dar.date AS date 
                FROM door_access_record dar
                WHERE dar.date BETWEEN :startDate AND :endDate 
                AND NOT EXISTS (
                    SELECT user_id  
                    FROM user_has_lunch uhl
                    WHERE dar.user_id = uhl.user_id 
                    AND dar.date = uhl.dt
                )
                UNION ALL
                SELECT uhl.user_id AS user_id, uhl.dt AS date 
                FROM user_has_lunch uhl
                WHERE uhl.dt BETWEEN :startDate AND :endDate
            ) combined
            JOIN user u ON combined.user_id = u.id
            JOIN lunch l ON combined.date = l.date
            GROUP BY u.name, u.staff_id
        )
        SELECT 
            name, 
            staff_id, 
            amount, 
            SUM(amount) OVER () AS total_amount
        FROM calculated_data
        ORDER BY staff_id
        """)
    List<Object[]> getWeeklyEmployeeOwnCost(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



    @Query(nativeQuery = true, value = """
        WITH calculated_data AS (
            SELECT 
                u.name AS name, 
                u.staff_id AS staff_id,
 
                SUM(l.price - (l.company_rate / 100) * l.price) AS amount
            FROM (
                SELECT dar.user_id AS user_id, dar.date AS date 
                FROM door_access_record dar
                WHERE 
                MONTH(dar.date) = :month
                AND YEAR(dar.date) = :year 
                AND NOT EXISTS (
                    SELECT user_id  
                    FROM user_has_lunch uhl
                    WHERE dar.user_id = uhl.user_id 
                    AND dar.date = uhl.dt
                )
                UNION ALL
                SELECT uhl.user_id AS user_id, uhl.dt AS date 
                FROM user_has_lunch uhl
                WHERE 
                MONTH(uhl.dt) = :month
                AND YEAR(uhl.dt) = :year
            ) combined
            JOIN user u ON combined.user_id = u.id
            JOIN lunch l ON combined.date = l.date
            GROUP BY u.name, u.staff_id
        )
        SELECT 
            name, 
            staff_id, 
            amount, 
            SUM(amount) OVER () AS total_amount
        FROM calculated_data
        ORDER BY staff_id
        """)
    List<Object[]> getMonthlyEmployeeOwnCost(@Param("month") int month, @Param("year") int year);

}