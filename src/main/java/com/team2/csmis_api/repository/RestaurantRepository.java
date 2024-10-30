package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    public Optional<Restaurant> findByName(String name);

    @Query("SELECT r FROM Restaurant r WHERE r.isDeleted = false")
    public List<Restaurant> getAllRestaurants();

    @Modifying
    @Transactional
    @Query("UPDATE Restaurant r SET r.isDeleted=true WHERE r.id=?1")
    public void deleteRestaurant(Integer id);

    public Restaurant getRestaurantById(int id);

}