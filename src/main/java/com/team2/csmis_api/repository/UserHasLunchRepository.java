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

    @Query("SELECT uhl FROM UserHasLunch uhl WHERE DATE(uhl.dt) = CURDATE()")
    List<UserHasLunch> findByCurrentDate();
}
