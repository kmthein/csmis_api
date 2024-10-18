package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.AnnouncementDTO;
import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/announcements")

public class AnnouncementController {

    @Autowired
    AnnouncementService announcementService;

    @PostMapping("")
    public ResponseEntity<?> saveAnnouncement(@ModelAttribute Announcement announcement,
                                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            announcementService.addAnnouncement(announcement, files);
            return ResponseEntity.ok("Announcement added successfully!");
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

    @GetMapping("{id}")
    public ResponseEntity<AnnouncementDTO> showById(@PathVariable("id") Integer id) {
        return announcementService.showByAnnouncementId(id)
                .map(AnnouncementDTO -> ResponseEntity.ok(AnnouncementDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAnnouncement(
            @PathVariable("id") Integer id,
            @ModelAttribute AnnouncementDTO announcementDTO,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        AnnouncementDTO updatedAnnouncementDTO = announcementService.updateAnnouncement(id, announcementDTO, files);
        String successMessage = String.format("Announcement with ID %d updated successfully!", id);
        return ResponseEntity.ok(successMessage);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable("id") Integer id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}
