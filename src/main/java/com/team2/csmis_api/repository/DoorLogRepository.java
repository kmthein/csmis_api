package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.DoorAccessRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            "AND CAST(d.date AS LocalDate) BETWEEN :startDate AND :endDate")
    List<DoorAccessRecord> findRegisteredAteByWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE d.user.id = ul.user.id " +
            "AND FUNCTION('MONTH', d.date) = :month " +
            "AND FUNCTION('YEAR', d.date) = :year")
    List<DoorAccessRecord> findRegisteredAteByMonth(@Param("month") int month, @Param("year") int year);


    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE d.user.id = ul.user.id " +
            "AND FUNCTION('YEAR', d.date) = :year")
    List<DoorAccessRecord> findRegisteredAteByYear(@Param("year") int year);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE ul.user.id IS NULL " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', :date)")
    List<DoorAccessRecord> findUnRegisteredAteByDate(@Param("date") LocalDate date);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE ul.user.id IS NULL " +
            "AND CAST(d.date AS LocalDate) BETWEEN :startDate AND :endDate")
    List<DoorAccessRecord> findUnRegisteredAteByWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE ul.user.id IS NULL " +
            "AND FUNCTION('MONTH', d.date) = :month " +
            "AND FUNCTION('YEAR', d.date) = :year")
    List<DoorAccessRecord> findUnRegisteredAteByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT d FROM DoorAccessRecord d " +
            "LEFT JOIN UserHasLunch ul ON d.user.id = ul.user.id " +
            "AND FUNCTION('DATE', d.date) = FUNCTION('DATE', ul.dt) " +
            "WHERE ul.user.id IS NULL " +
            "AND FUNCTION('YEAR', d.date) = :year")
    List<DoorAccessRecord> findUnRegisteredAteByYear(@Param("year") int year);



}
