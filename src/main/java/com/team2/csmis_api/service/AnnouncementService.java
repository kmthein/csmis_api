package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.AnnouncementDTO;
import com.team2.csmis_api.dto.FileDTO;
import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.entity.FileData;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.AnnouncementRepository;
import com.team2.csmis_api.repository.FileRepository;
import com.team2.csmis_api.repository.UserRepository;
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
    private AnnouncementRepository announcementRepo;

    @Autowired
    FileService fileService;

    @Autowired
    FileRepository fileRepo;

    @Autowired
    private ModelMapper modelMapper;

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
        AnnouncementDTO announcementDTO = modelMapper.map(savedAnnouncement, AnnouncementDTO.class);
        announcementDTO.setAdminId(savedAnnouncement.getUser().getId());

        return announcementDTO;
    }



    public AnnouncementDTO convertToAnnouncementDto(Announcement announce) {
        AnnouncementDTO announcementDTO=modelMapper.map(announce, AnnouncementDTO.class);
        announcementDTO.setAdminId(announce.getUser().getId());
        announcementDTO.setDate(announce.getDate());
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
