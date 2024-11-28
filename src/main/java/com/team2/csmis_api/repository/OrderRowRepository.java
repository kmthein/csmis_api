package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.OrderRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRowRepository extends JpaRepository<OrderRow, Integer> {
    // Custom query to fetch order rows within a given week (7 days around the selected date)
    @Query("SELECT o FROM OrderRow o WHERE o.lunchDate BETWEEN :startDate AND :endDate")
    List<OrderRow> findOrderRowsForWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}