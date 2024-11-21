package com.team2.csmis_api.repository;


import com.team2.csmis_api.entity.UserHasLunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserHasLunchRepository extends JpaRepository<UserHasLunch, Integer> {


    List<UserHasLunch> findByUserId(Integer userId);


    @Query("SELECT u.dt FROM UserHasLunch u WHERE u.user.id = :userId")
    List<Date> findDtByUserId(@Param("userId") int userId);

    Optional<UserHasLunch> findByUserIdAndDt(Long userId, Date dt);


    @Query("SELECT COUNT(u) FROM UserHasLunch u WHERE u.user.id = :userId AND MONTH(u.dt) = MONTH(CURRENT_DATE) AND YEAR(u.dt) = YEAR(CURRENT_DATE)")
    int countRegisteredDaysForMonth(@Param("userId") Integer userId);

    @Query("SELECT u FROM UserHasLunch u WHERE MONTH(u.dt) = :month AND YEAR(u.dt) = :year")
    List<UserHasLunch> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT u FROM UserHasLunch u WHERE u.dt BETWEEN :startOfWeek AND :endOfWeek")
    List<UserHasLunch> findByDtBetween(@Param("startOfWeek") Date startOfWeek, @Param("endOfWeek") Date endOfWeek);

    @Query("SELECT uhl FROM UserHasLunch uhl WHERE uhl.dt BETWEEN :startDate AND :endDate")
    List<UserHasLunch> findUserHasLunchForPreviousWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT uhl FROM UserHasLunch uhl WHERE MONTH(uhl.dt) = :month AND YEAR(uhl.dt) = :year")
    List<UserHasLunch> findUserHasLunchForMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT uhl FROM UserHasLunch uhl JOIN uhl.user u WHERE uhl.dt BETWEEN :startOfWeek AND :endOfWeek AND u.department.id = :departmentId")
    List<UserHasLunch> findUserHasLunchForPreviousWeekByDepartment(@Param("startOfWeek") Date startOfWeek, @Param("endOfWeek") Date endOfWeek, @Param("departmentId") Integer departmentId);


    @Query("SELECT uhl FROM UserHasLunch uhl JOIN uhl.user u WHERE MONTH(uhl.dt) = :month AND YEAR(uhl.dt) = :year AND u.department.id = :departmentId")
    List<UserHasLunch> findUserHasLunchForMonthAndDepartment(@Param("month") int month, @Param("year") int year, @Param("departmentId") int departmentId);

    @Query("SELECT u FROM UserHasLunch u WHERE YEAR(u.dt) = :year")
    List<UserHasLunch> findUserHasLunchForYear(@Param("year") int year);

    @Query("SELECT uhl FROM UserHasLunch uhl JOIN uhl.user u WHERE YEAR(uhl.dt) = :year AND u.department.id = :departmentId")
    List<UserHasLunch> findUserHasLunchForYearAndDepartment(@Param("year") int year, @Param("departmentId") int departmentId);

}
