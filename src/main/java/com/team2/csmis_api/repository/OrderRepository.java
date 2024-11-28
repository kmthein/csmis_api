package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Order;
import com.team2.csmis_api.entity.OrderRow;
import com.team2.csmis_api.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
//    Optional<Order> findByOrderDateBetween(LocalDate startOfWeek, LocalDate endOfWeek);
//
//
//    @Query("SELECT o FROM Order o WHERE o.restaurant.name = :restaurantName AND o.orderDate = :orderDate")
//    Optional<Order> findByRestaurantNameAndOrderDate(@Param("restaurantName") String restaurantName,
//                                                     @Param("orderDate") LocalDate orderDate);

    @Query("SELECT o FROM OrderRow o WHERE o.lunchDate IN :dates")
    List<OrderRow> findOrderRowsByDates(@Param("dates") List<LocalDate> dates);
    @Query("SELECT COALESCE(SUM(o.quantity), 0) FROM OrderRow o WHERE o.lunchDate = :date")
    long getTotalQuantityByDate(@Param("date") LocalDate date);

    @Query("SELECT o FROM Order o WHERE o.orderDate = :orderDate")
    Optional<Order> findByOrderDate(@Param("orderDate") LocalDate orderDate);

    @Query("SELECT o.restaurant FROM Order o WHERE o.orderDate = :orderDate")
    List<Restaurant> findRestaurantByOrderDate(@Param("orderDate") LocalDate orderDate);
}
