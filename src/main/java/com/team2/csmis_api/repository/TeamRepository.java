package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Department;
import com.team2.csmis_api.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    @Query("SELECT d FROM Team d WHERE d.name = :name")
    Team findTeamByName(@Param("name") String name);
}
