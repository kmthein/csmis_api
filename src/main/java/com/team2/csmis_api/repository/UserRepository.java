package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    public List<User> getAllActiveUsers();
    public User findByStaffId(String staffId);
    public User getUserById(int id);
}
