package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Department;
import com.team2.csmis_api.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("SELECT d FROM Department d WHERE d.name = :name")
    Department findDepartmentByName(@Param("name") String name);

    Department findByNameAndDivision(String name, Division division);
}
