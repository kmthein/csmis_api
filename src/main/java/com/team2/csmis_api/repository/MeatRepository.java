package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Meat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeatRepository extends JpaRepository<Meat,Integer> {
    @Query("SELECT m FROM Meat m WHERE m.isDeleted = false")
    List<Meat> findAllByIsDeletedFalse();

    @Query("SELECT COUNT(m) > 0 FROM Meat m WHERE LOWER(m.name) = LOWER(:name) AND m.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
    @Query("SELECT COUNT(m) = 0 FROM Meat m WHERE LOWER(m.name) = LOWER(:name) AND m.isDeleted = false")
    boolean isNameUnique(@Param("name") String name);
    @Query("SELECT m FROM Meat m WHERE m.isDeleted = false")
    List<Meat> findAllActiveMeats();
}
