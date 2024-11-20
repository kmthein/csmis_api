package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Integer> {
    @Query("SELECT d FROM Division d WHERE d.name = :name")
    Division findDivisionByName(@Param("name") String name);
}
