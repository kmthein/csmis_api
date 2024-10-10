package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByStaffId(String staffId);
    User findByName(String username);

    User findById(Long userId);
}
