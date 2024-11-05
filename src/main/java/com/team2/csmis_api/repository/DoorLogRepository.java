package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.DoorAccessRecord;
import com.team2.csmis_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoorLogRepository extends JpaRepository<DoorAccessRecord,Integer> {

    @Query("SELECT d FROM DoorAccessRecord d")
    public List<DoorAccessRecord> getAllDoorAccessRecords();

    public DoorAccessRecord getDoorLogById(int id);
    boolean existsByDoorLogNoAndDate(Integer doorLogNo, LocalDateTime date);
}
