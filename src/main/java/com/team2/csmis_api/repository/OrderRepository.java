package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Order;
import com.team2.csmis_api.entity.OrderRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

//    @Query("SELECT o FROM Order o JOIN FETCH o.rows WHERE o.id = :id")
//    Optional<Order> findByIdWithRows(@Param("id") Long id);

    // Find an order by its lunch date
    @Query("SELECT o FROM Order o WHERE o.orderDate = :lunchDate")
    Optional<Order> findByLunchDate(@Param("lunchDate") LocalDate lunchDate);

//    @Query("SELECT orw FROM OrderRow orw WHERE orw.lunchDate = :lunchDate")
//    Optional<OrderRow> findOrderRowByLunchDate(LocalDate lunchDate);

//    @Query("SELECT o FROM Order o JOIN o.rows r WHERE r.lunchDate = :lunchDate")
//    Optional<Order> findOrderRowByLunchDate(@Param("lunchDate") LocalDate lunchDate);

    @Query("SELECT o FROM Order o JOIN o.rows r WHERE r.lunchDate = :lunchDate")
    Optional<Order> findOrderByLunchDate(@Param("lunchDate") LocalDate lunchDate);
}

