package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Holiday;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HolidayRepository extends JpaRepository<Holiday,Integer> {

    @Query("select h from Holiday h where h.isDeleted<>true")
    public List<Holiday> getAllHolidays();

    @Modifying
    @Transactional
    @Query("update Holiday h set h.isDeleted=true where h.id=:id")
    public void deleteHoliday(Integer id);
}
