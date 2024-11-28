package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.AnnouncementDTO;
import com.team2.csmis_api.entity.UserHasAnnouncement;
import com.team2.csmis_api.repository.UserHasAnnouncementRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserHasAnnouncementService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserHasAnnouncementRepository userHasAnnouncementRepository;

    public List<AnnouncementDTO> getLatestAnnouncements() {
        List<UserHasAnnouncement> announcements = userHasAnnouncementRepository.findLatestAnnouncements();

        // Limit to the latest 6 announcements
        return announcements.stream()
                .limit(6)
                .map(this::convertToAnnouncementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves unseen announcements for a specific user by user ID.
     */
    public List<AnnouncementDTO> findUnseenAnnouncementsByUserId(Integer userId) {
        List<UserHasAnnouncement> unseenAnnouncements = userHasAnnouncementRepository.findByIsSeenFalseAndUser_Id(userId);
        return unseenAnnouncements.stream()
                .map(this::convertToAnnouncementDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a UserHasAnnouncement entity to an AnnouncementDTO.
     */
    private AnnouncementDTO convertToAnnouncementDTO(UserHasAnnouncement userHasAnnouncement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(userHasAnnouncement.getAnnouncement().getId());
        dto.setDate(userHasAnnouncement.getAnnouncement().getDate());
        dto.setContent(userHasAnnouncement.getAnnouncement().getContent());
        dto.setAdminId(userHasAnnouncement.getUser().getId());
        dto.setCreatedAt(userHasAnnouncement.getAnnouncement().getCreatedAt());
        dto.setTitle(userHasAnnouncement.getAnnouncement().getTitle());
        dto.setSeen(userHasAnnouncement.getIsSeen());
        return dto;
    }

    /**
     * Finds all UserHasAnnouncement entries.
     */
    public List<UserHasAnnouncement> findAll() {
        return userHasAnnouncementRepository.findAll();
    }

    /**
     * Finds a specific UserHasAnnouncement by ID.
     */
    public Optional<UserHasAnnouncement> findById(Integer id) {
        return userHasAnnouncementRepository.findById(id);
    }

    /**
     * Saves a UserHasAnnouncement entry to the database.
     */
    public UserHasAnnouncement save(UserHasAnnouncement userHasAnnouncement) {
        return userHasAnnouncementRepository.save(userHasAnnouncement);
    }

    /**
     * Deletes a UserHasAnnouncement entry by ID.
     */
    public void deleteById(Integer id) {
        userHasAnnouncementRepository.deleteById(id);
    }
}
