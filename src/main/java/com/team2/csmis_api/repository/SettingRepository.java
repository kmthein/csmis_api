package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Settings, Integer> {
    @Query("SELECT s FROM Settings s ORDER BY s.createdAt DESC")
    Settings findLatestSettings();

    Settings findTopByOrderByIdAsc();

    Settings findTopByOrderByIdDesc();
}
