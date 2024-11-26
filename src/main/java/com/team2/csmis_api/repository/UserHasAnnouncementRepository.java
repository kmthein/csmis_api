package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHasAnnouncementRepository extends JpaRepository<UserHasAnnouncement, Integer> {
    @Query("SELECT uhs FROM UserHasAnnouncement uhs WHERE uhs.user.id = :userId ORDER BY uhs.announcement.createdAt DESC")
    List<UserHasAnnouncement> findByIsSeenFalseAndUser_Id(Integer userId);

    @Query("SELECT uha FROM UserHasAnnouncement uha ORDER BY uha.announcement.date DESC")
    List<UserHasAnnouncement> findLatestAnnouncements();

    UserHasAnnouncement findByAnnouncementAndUser(Announcement announcement, User user);
}
