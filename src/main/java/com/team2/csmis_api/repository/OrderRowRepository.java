package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Order;
import com.team2.csmis_api.entity.OrderRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRowRepository extends JpaRepository<OrderRow, Integer> {
    List<OrderRow> findByOrderId(Long orderId);
    @Query("SELECT o FROM OrderRow o WHERE o.lunchDate BETWEEN :startDate AND :endDate AND " +
            "FUNCTION('DAYOFWEEK', o.lunchDate) NOT IN (1, 7)") // 1 = Sunday, 7 = Saturday in MySQL
    List<OrderRow> findAllByLunchDateBetweenAndWeekdayOnly(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Query to get Order by matching lunchDate from OrderRow
    @Query("SELECT o FROM Order o JOIN o.rows r WHERE r.lunchDate = :lunchDate")
    Optional<Order> findByLunchDate(@Param("lunchDate") LocalDate lunchDate);


}

