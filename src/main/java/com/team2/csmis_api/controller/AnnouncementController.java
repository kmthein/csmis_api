package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.AnnouncementDTO;
import com.team2.csmis_api.dto.LunchDTO;
import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.service.AnnouncementService;
import com.team2.csmis_api.service.LunchService;
import com.team2.csmis_api.service.UserHasAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/announcements")

public class AnnouncementController {

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    private UserHasAnnouncementService userHasAnnouncementService;

    @GetMapping("/unseen/{userId}")
    public List<AnnouncementDTO> getUnseenAnnouncementsByUserId(@PathVariable Integer userId) {
        return userHasAnnouncementService.findUnseenAnnouncementsByUserId(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> saveAnnouncement(@ModelAttribute Announcement announcement,
                                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            AnnouncementDTO announcementDTO = announcementService.addAnnouncement(announcement, files);
            if(announcementDTO != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Announcement added successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Announcement can't be added!");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("")
    public List<AnnouncementDTO> showAllAnnouncements() {
        return announcementService.getAllAnnouncementsWithFiles();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnnouncement(
            @PathVariable("id") Integer id,
            @ModelAttribute AnnouncementDTO announcementDTO,
            @RequestParam(value = "newFiles", required = false) MultipartFile[] files,
            @RequestParam(value = "deleteFileIds", required = false) List<Integer> deleteFileIds) throws IOException {
        AnnouncementDTO res = announcementService.updateAnnouncement(id, announcementDTO, files, deleteFileIds);
        if(res != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Announcement updated successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Announcement can't be updated!");
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable("id") Integer id) {
        Announcement announcement = announcementService.deleteAnnouncement(id);
        if(announcement != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Announcement delete successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Announcement can't be deleted!");
        }
    }
}
