package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Restaurant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LunchRepository extends JpaRepository<Lunch, Integer> {
    @Query("SELECT l FROM Lunch l WHERE l.isDeleted = false")
    public List<Lunch> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE Lunch l SET l.isDeleted=true WHERE l.id=?1")
    public void deleteLunch(Integer id);
}