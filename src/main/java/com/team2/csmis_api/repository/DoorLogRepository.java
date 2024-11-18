package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.DoorAccessRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface DoorLogRepository extends JpaRepository<DoorAccessRecord,Integer> {
    @Query("SELECT d FROM DoorAccessRecord d")
    public List<DoorAccessRecord> getAllDoorAccessRecords();

    public DoorAccessRecord getDoorLogById(int id);
    boolean existsByDoorLogNoAndDate(Integer doorLogNo, LocalDateTime date);


    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', :date)")
    List<DoorAccessRecord> findRegisteredAteByDate(@Param("date") LocalDate date);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE d.user.id = ul.user.id " +
            "AND d.date BETWEEN :startDate AND :endDate")
    List<DoorAccessRecord> findRegisteredAteByWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE d.user.id = ul.user.id " +
            "AND FUNCTION('MONTH', d.date) = :#{#date.monthValue} " +
            "AND FUNCTION('YEAR', d.date) = :#{#date.year}")
    List<DoorAccessRecord> findRegisteredAteByMonth(@Param("date") YearMonth date);


    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE d.user.id = ul.user.id " +
            "AND FUNCTION('YEAR', d.date) = :year")
    List<DoorAccessRecord> findRegisteredAteByYear(@Param("year") int year);
}
