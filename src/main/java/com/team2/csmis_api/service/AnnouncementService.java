package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.AnnouncementDTO;
import com.team2.csmis_api.dto.FileDTO;
import com.team2.csmis_api.entity.Announcement;
import com.team2.csmis_api.entity.FileData;
import com.team2.csmis_api.entity.User;
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

            List<Integer> fileIds = new ArrayList<>();
            List<FileDTO> fileDTOS = new ArrayList<>();
            for (FileData file : announcement.getFileData()) {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFilePath(file.getFilePath());
                fileDTO.setType(file.getFileType());
                fileDTO.setId(file.getId());
                fileDTOS.add(fileDTO);
            }
            dto.setFiles(fileDTOS);
            announcementDTOs.add(dto);
        }

        return announcementDTOs;
    }



    public Optional<AnnouncementDTO> showByAnnouncementId(Integer id) {
        return announcementRepo.findById(id)
                .map(announcement -> {
                    AnnouncementDTO dto = convertToAnnouncementDto(announcement);

                    List<Integer> fileIds = new ArrayList<>();
                    for (FileData file : announcement.getFileData()) {
                        fileIds.add(file.getId());
                    }
                    dto.setFileIds(fileIds);

                    return dto;
                });
    }


    @Transactional
    public AnnouncementDTO updateAnnouncement(Integer id, AnnouncementDTO announcementDTO, MultipartFile[] files) throws IOException {
        Announcement existingAnnouncement = announcementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with ID: " + id));

        modelMapper.map(announcementDTO, existingAnnouncement);

        List<FileData> existingFiles = existingAnnouncement.getFileData();

        if (files != null && files.length > 0) {
            for (FileData file : existingFiles) {
                file.setIsDeleted(true);
            }

            List<FileData> newFileDataList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    FileDTO savedFileDTO = fileService.uploadFile(new FileData(), file);
                    newFileDataList.add(modelMapper.map(savedFileDTO, FileData.class));
                }
            }

            existingAnnouncement.setFileData(newFileDataList);
            existingAnnouncement.setDate(LocalDate.now());
        } else {
            for (FileData file : existingFiles) {
                file.setIsDeleted(true);
            }
        }

        Announcement updatedAnnouncement = announcementRepo.save(existingAnnouncement);

        return modelMapper.map(updatedAnnouncement, AnnouncementDTO.class);
    }


    public void deleteAnnouncement(Integer id) {
        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with ID: " + id));

        announcement.setIsDeleted(true);
        announcementRepo.save(announcement);

        for (FileData file : announcement.getFileData()) {
            file.setIsDeleted(true);
        }

        fileRepo.saveAll(announcement.getFileData());
    }

}
