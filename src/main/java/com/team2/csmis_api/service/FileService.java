package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.FileDTO;
import com.team2.csmis_api.entity.FileData;
import com.team2.csmis_api.repository.FileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    FileRepository fileRepo;

    @Autowired
    private ModelMapper modelMapper;

    private static final String UPLOAD_DIR = "uploads";

    @Transactional
    public FileDTO uploadFile(FileData fileData, MultipartFile file) throws IOException {
        String fileName = "";
        if (file != null && !file.isEmpty()) {
            fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(UPLOAD_DIR + "/" + fileName);
            Files.write(path, file.getBytes());
            fileData.setFilePath("uploads\\" + fileName);
            fileData.setFileType(file.getContentType());
            System.out.println("File saved at: " + path.toString());
        }

        FileData savedFile = fileRepo.save(fileData);
        FileDTO fileDTO = modelMapper.map(savedFile, FileDTO.class);
        return fileDTO;
    }
}
