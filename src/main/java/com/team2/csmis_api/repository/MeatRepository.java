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
    @Query("""
    SELECT CASE WHEN COUNT(m) = 0 THEN true ELSE false END 
    FROM Meat m 
    WHERE LOWER(REPLACE(m.name, ' ', '')) = LOWER(REPLACE(:name, ' ', '')) 
      AND m.isDeleted = false
""")
    boolean isNameUnique(@Param("name") String name);


    @Query("SELECT m FROM Meat m WHERE m.isDeleted = false")
    List<Meat> findAllActiveMeats();
    Meat findByName(String name);
//    @Query("""
//    SELECT COUNT(m) = 0
//    FROM Meat m
//    WHERE LOWER(TRIM(BOTH FROM m.name)) = LOWER(TRIM(BOTH FROM :name))
//      AND m.isDeleted = false
//      AND (:idToExclude IS NULL OR m.id != :idToExclude)
//""")
//    long countByNormalizedNameExcludingId(String normalizedName, Integer idToExclude);
}
