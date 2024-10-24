package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Announcement;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Integer> {

    @Query("SELECT a FROM Announcement a WHERE a.isDeleted <> true ORDER BY a.updatedAt DESC")
    public List<Announcement> getAllAnnouncementsWithFiles();

    @Modifying
    @Transactional
    @Query("update Announcement a set a.isDeleted=true where a.id=:id")
    public void deleteAnnouncement(Integer id);
}
