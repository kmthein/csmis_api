package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.AnnouncementDTO;
import com.team2.csmis_api.dto.FileDTO;
import com.team2.csmis_api.dto.SuggestionDTO;
import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.entity.FileData;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasAnnouncement;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.AnnouncementRepository;
import com.team2.csmis_api.repository.FileRepository;
import com.team2.csmis_api.repository.UserHasAnnouncementRepository;
import com.team2.csmis_api.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserHasAnnouncementRepository userHasAnnouncementRepository;

    @Autowired
    private AnnouncementRepository announcementRepo;

    @Autowired
    FileService fileService;

    @Autowired
    FileRepository fileRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    public String generateAnnouncementEmailContent(Announcement announcement) {
        StringBuilder content = new StringBuilder();

        // Start with basic layout and styling
        content.append("<div style='background-color: white; padding: 16px; border-radius: 8px;'>");
        content.append("<div style='display: flex; gap: 16px;'>");

        // Title and Date
        content.append("<div style='width: 100%;'>");
        content.append("<h4 style='margin: 0;'>" + announcement.getTitle() + "</h4>");

        // Content
        content.append("<div style='margin-top: 16px;'>");
        content.append("<p>" + announcement.getContent() + "</p>");
        content.append("</div>");

        // Filter files where isDeleted is false and include them in the email
        if (announcement.getFileData() != null && !announcement.getFileData().isEmpty()) {
            content.append("<div style='margin-top: 16px;'>");

            // Only process files that are not deleted
            announcement.getFileData().stream()
                    .filter(file -> !file.getIsDeleted()) // Filter out deleted files
                    .forEach(file -> {
                        String filePath = file.getFilePath();
                        String fileType = file.getFileType();

                        if ("image/jpeg".equals(fileType) || "image/png".equals(fileType) || "image/jpg".equals(fileType)) {
                            content.append("<img src='" + filePath + "' style='width: 100%; object-fit: contain; margin-bottom: 8px;' />");
                        } else if ("application/pdf".equals(fileType)) {
                            content.append("<a href='" + filePath + "' target='_blank'>View PDF</a>");
                        } else if ("video/mp4".equals(fileType)) {
                            content.append("<video width='100%' height='240' controls>");
                            content.append("<source src='" + filePath + "' type='" + fileType + "' />");
                            content.append("Your browser does not support the video tag.");
                            content.append("</video>");
                        }
                    });

            content.append("</div>");
        }

        content.append("</div></div>");
        return content.toString();
    }

    public AnnouncementDTO getAnnouncementById(Integer id) {
        Announcement announcement = announcementRepo.getAnnouncementById(id);
        AnnouncementDTO announcementDTO = convertToAnnouncementDto(announcement);
        List<FileDTO> files = new ArrayList<>();
        for (FileData file : announcement.getFileData()) {
            if (!file.getIsDeleted()) {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setId(file.getId());
                fileDTO.setFilePath(file.getFilePath());
                fileDTO.setFiletype(file.getFileType());
                fileDTO.setIsDeleted(file.getIsDeleted());
                files.add(fileDTO);
            }
        }
        announcementDTO.setFiles(files);
        return announcementDTO;
    }

    public AnnouncementDTO getAnnouncementByIdAndMakeSeen(Integer id, Integer userId) {
        Announcement announcement = announcementRepo.findById(id).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        UserHasAnnouncement userAnnouncement = userHasAnnouncementRepository.findByAnnouncementAndUser(announcement, user);
        AnnouncementDTO announcementDTO = new AnnouncementDTO();
        if(userAnnouncement != null) {
            userAnnouncement.setIsSeen(true);
            userAnnouncement = userHasAnnouncementRepository.save(userAnnouncement);
            announcementDTO = convertToAnnouncementDto(announcement);
            announcementDTO.setSeen(true);
        }
        return announcement != null ? announcementDTO : null;
    }

    public AnnouncementDTO addAnnouncement(Announcement announce, MultipartFile[] files) throws IOException {
        User adminId = userRepo.findById(announce.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        announce.setUser(adminId);
        announce.setDate(LocalDate.now());

        List<FileData> fileDataList = new ArrayList<>();

        try {
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        FileData fileData = new FileData();
                        FileDTO savedFileDTO = fileService.uploadFile(fileData, file);
                        fileDataList.add(modelMapper.map(savedFileDTO, FileData.class));
                    }
                }
                announce.setFileData(fileDataList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Announcement savedAnnouncement = announcementRepo.save(announce);
        if(savedAnnouncement != null) {
            String subject = "New Announcement: " + savedAnnouncement.getTitle();
            String emailContent = generateAnnouncementEmailContent(savedAnnouncement);
            List<User> mailNotiOnUsers = userRepo.getMailNotiOnUsers();
            for(User user: mailNotiOnUsers) {
                try {
                    emailService.sendEmail(user.getEmail(), subject, emailContent);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        AnnouncementDTO announcementDTO = modelMapper.map(savedAnnouncement, AnnouncementDTO.class);
        announcementDTO.setAdminId(savedAnnouncement.getUser().getId());

        List<User> users = userRepo.getAllActiveUsers();
        for (User user : users) {

            UserHasAnnouncement userHasAnnouncement = new UserHasAnnouncement();
            userHasAnnouncement.setUser(user);
            userHasAnnouncement.setAnnouncement(savedAnnouncement);  // Associate the announcement with the user
            userHasAnnouncementRepository.save(userHasAnnouncement);  // Save the UserHasAnnouncement entry
        }

        notificationService.sendAnnouncementNotification("{\"message\": \"New announcement received\"}");

        return announcementDTO;
    }

    public AnnouncementDTO convertToAnnouncementDto(Announcement announce) {
        AnnouncementDTO announcementDTO=modelMapper.map(announce, AnnouncementDTO.class);
        announcementDTO.setAdminId(announce.getUser().getId());
        announcementDTO.setDate(announce.getDate());
        announcementDTO.setId(announce.getId());
        announcementDTO.setTitle(announce.getTitle());
        announcementDTO.setCreatedAt(announce.getCreatedAt());
        return announcementDTO;
    }

    public List<AnnouncementDTO> getAllAnnouncementsWithFiles() {
        List<Announcement> announcements = announcementRepo.getAllAnnouncementsWithFiles();
        List<AnnouncementDTO> announcementDTOs = new ArrayList<>();

        for (Announcement announcement : announcements) {
            AnnouncementDTO dto = convertToAnnouncementDto(announcement);

            List<FileDTO> files = new ArrayList<>();
            for (FileData file : announcement.getFileData()) {
                if (!file.getIsDeleted()) {
                    FileDTO fileDTO = new FileDTO();
                    fileDTO.setId(file.getId());
                    fileDTO.setFilePath(file.getFilePath());
                    fileDTO.setFiletype(file.getFileType());
                    fileDTO.setIsDeleted(file.getIsDeleted());
                    files.add(fileDTO);
                }
            }
            dto.setFiles(files);
            announcementDTOs.add(dto);
        }

        return announcementDTOs;
    }

    @Transactional
    public AnnouncementDTO updateAnnouncement(Integer id, AnnouncementDTO announcementDTO, MultipartFile[] files, List<Integer> filesToDelete) throws IOException {

        Announcement existingAnnouncement = announcementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with ID: " + id));

        modelMapper.map(announcementDTO, existingAnnouncement);

        List<FileData> existingFiles = existingAnnouncement.getFileData();

        if (filesToDelete != null && !filesToDelete.isEmpty()) {
            for (FileData file : existingFiles) {
                if (filesToDelete.contains(file.getId())) {
                    file.setIsDeleted(true);
                }
            }
        }

        if (files != null && files.length > 0) {
            List<FileData> newFileDataList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    FileDTO savedFileDTO = fileService.uploadFile(new FileData(), file);
                    newFileDataList.add(modelMapper.map(savedFileDTO, FileData.class));
                }
            }
            existingFiles.addAll(newFileDataList);
        }
        Optional<User> optAdmin = userRepo.findById(announcementDTO.getAdminId());
        if(optAdmin.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with this ID.");
        } else {
            existingAnnouncement.setUser(optAdmin.get());
        }
        existingAnnouncement.setDate(LocalDate.now());

        Announcement updatedAnnouncement = announcementRepo.save(existingAnnouncement);
        if(updatedAnnouncement != null) {
            String subject = "Announcement Updated: " + updatedAnnouncement.getTitle();
            String emailContent = generateAnnouncementEmailContent(updatedAnnouncement);
            List<User> mailNotiOnUsers = userRepo.getMailNotiOnUsers();
            for(User user: mailNotiOnUsers) {
                try {
                    emailService.sendEmail(user.getEmail(), subject, emailContent);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return modelMapper.map(updatedAnnouncement, AnnouncementDTO.class);
    }

    public Announcement deleteAnnouncement(Integer id) {
        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with ID: " + id));

        announcement.setIsDeleted(true);
        announcementRepo.save(announcement);

        for (FileData file : announcement.getFileData()) {
            file.setIsDeleted(true);
        }

        fileRepo.saveAll(announcement.getFileData());

        return announcement;
    }

}
