package com.team2.csmis_api.repository;

import com.team2.csmis_api.dto.OrderDTO;
import com.team2.csmis_api.dto.OrderRowDTO;
import com.team2.csmis_api.entity.Order;
import com.team2.csmis_api.entity.OrderRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRowRepository extends JpaRepository<OrderRow, Integer> {
    // Custom query to fetch order rows within a given week (7 days around the selected date)
    @Query("SELECT new com.team2.csmis_api.dto.OrderDTO(o.id, o.orderDate, o.message, o.restaurant.id, o.restaurant.name, o.user.id) FROM OrderRow or JOIN or.order o WHERE or.lunchDate = :date")
    OrderDTO findOrderByDate(@Param("date") LocalDate date);
    @Query("SELECT o FROM OrderRow o WHERE o.lunchDate BETWEEN :startDate AND :endDate")
    List<OrderRow> findOrderRowsForWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT o FROM OrderRow o WHERE o.lunchDate = :date")
    long getOrderRowQuantity(@Param("date") Date date);
    @Query("SELECT new com.team2.csmis_api.dto.OrderRowDTO(or.id, or.lunchDate, or.quantity) FROM OrderRow or JOIN or.order o WHERE o.id = :id")
    List<OrderRowDTO> getOrderRowsByOrderId(@Param("id") Integer id);
}