package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer> {

    Location findById(int id);

}
