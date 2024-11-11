package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.DoorAccessRecordDTO;
import com.team2.csmis_api.entity.DoorAccessRecord;
import com.team2.csmis_api.entity.Status;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.DoorLogRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoorLogService {

    @Autowired
    private ExcelForDoorLogService excelForDoorLogService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DoorLogRepository doorLogRepo;

    @Autowired
    private ModelMapper mapper;

    @Transactional
    public List<DoorAccessRecord> saveDoorLogToDatabase(MultipartFile file, Integer adminId) {
        List<DoorAccessRecord> doorAccessRecords = new ArrayList<>();
        if (ExcelForDoorLogService.isValidExcelFile(file)) {
            try {
                doorAccessRecords = excelForDoorLogService.getDoorLogDataFromExcel(file.getInputStream());

                User admin = userRepo.findById(adminId).orElseThrow(() ->
                        new IllegalArgumentException("Admin not found")
                );

                List<DoorAccessRecord> recordsToSave = new ArrayList<>();

                for (DoorAccessRecord doorAccessRecord : doorAccessRecords) {
                    boolean exists = doorLogRepo.existsByDoorLogNoAndDate(doorAccessRecord.getDoorLogNo(), doorAccessRecord.getDate());

                    if (!exists) {
                        doorAccessRecord.setAdmin(admin);
                        recordsToSave.add(doorAccessRecord);
                    }
                }

                doorLogRepo.saveAll(recordsToSave);
                return recordsToSave;

            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        }
        return new ArrayList<>();
    }


    public DoorAccessRecordDTO mapUserToDTO(DoorAccessRecord doorAccessRecord) {
        DoorAccessRecordDTO doorAccessRecordDTO = mapper.map(doorAccessRecord, DoorAccessRecordDTO.class);
        if(doorAccessRecord.getUser() != null) {
            doorAccessRecordDTO.setName(doorAccessRecord.getUser().getName());
        }
        if(doorAccessRecord.getLocation() != null) {
            doorAccessRecordDTO.setLocation(doorAccessRecord.getLocation().getBuilding());
        }
        if(doorAccessRecord.getIsDeleted() == true) {
            doorAccessRecordDTO.setStatus(Status.InActive.toString());
        } else {
            doorAccessRecordDTO.setStatus(Status.Active.toString());
        }
        return doorAccessRecordDTO;
    }


    public List<DoorAccessRecordDTO> getAllDoorAccessRecords() {
        List<DoorAccessRecord> doorAccessRecords = doorLogRepo.getAllDoorAccessRecords();
        List<DoorAccessRecordDTO> doorAccessRecordDTOList = new ArrayList<>();

        for(DoorAccessRecord doorAccessRecord: doorAccessRecords) {
            DoorAccessRecordDTO doorAccessRecordDTO = mapUserToDTO(doorAccessRecord);
            doorAccessRecordDTOList.add(doorAccessRecordDTO);
        }
        return doorAccessRecordDTOList;
    }

    public DoorAccessRecordDTO getDoorLogById(int id) {
        DoorAccessRecord doorAccessRecord = doorLogRepo.getDoorLogById(id);
        DoorAccessRecordDTO DoorAccessRecordDTO = mapUserToDTO(doorAccessRecord);
        return DoorAccessRecordDTO;
    }


}
