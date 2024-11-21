package com.team2.csmis_api.repository;


import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserHasLunchRepository extends JpaRepository<UserHasLunch, Integer> {

    List<UserHasLunch> findByUserId(Integer userId);

    @Query("SELECT u.dt FROM UserHasLunch u WHERE u.user.id = :userId")
    List<Date> findDtByUserId(@Param("userId") int userId);

    Optional<UserHasLunch> findByUserIdAndDt(Long userId, Date dt);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND FUNCTION('DATE', u.dt) = :date")
    List<UserHasLunch> findRegisteredNotEatDaily(@Param("date") LocalDate date);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND CAST(u.dt AS LocalDate) BETWEEN :startDate AND :endDate")
    List<UserHasLunch> findRegisteredNotEatWeekly(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND FUNCTION('MONTH', u.dt) = :month " +
            "AND FUNCTION('YEAR', u.dt) = :year")
    List<UserHasLunch> findRegisteredNotEatMonthly(@Param("month") int month, @Param("year") int year);

    @Query("SELECT u FROM UserHasLunch u " +
            "LEFT JOIN DoorAccessRecord d ON u.user.id = d.user.id " +
            "AND FUNCTION('DATE', u.dt) = FUNCTION('DATE', d.date) " +
            "WHERE d.user.id IS NULL " +
            "AND FUNCTION('YEAR', u.dt) = :year")
    List<UserHasLunch> findRegisteredNotEatYearly(@Param("year") int year);


}
