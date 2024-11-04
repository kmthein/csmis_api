package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Department;
import com.team2.csmis_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u")
    public List<User> getAllActiveUsers();
    public User findByStaffId(String staffId);
    User findByName(String username);
    public User getUserById(int id);
    User findById(Long userId);
    Optional<User> findByEmail(String email);

    @Query("SELECT d FROM User d WHERE d.doorLogNo = :doorLogNo")
    User findByDoorLogNo(@Param("doorLogNo") int doorLogNo);

}
