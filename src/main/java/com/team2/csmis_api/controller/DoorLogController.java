package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.DoorAccessRecordDTO;
import com.team2.csmis_api.service.DoorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/doorLogs")
public class DoorLogController {

    @Autowired
    private DoorLogService doorLogService;

    @PostMapping("excel")
    public ResponseEntity<?> uploadHolidaysData(@RequestParam("file") MultipartFile file,
                                                @RequestParam("adminId") Integer adminId) {
        doorLogService.saveDoorLogToDatabase(file, adminId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "DoorLog data uploaded and saved to database successfully"));
    }

    @GetMapping("")
    public List<DoorAccessRecordDTO> getAllDoorLog() {
        return doorLogService.getAllDoorAccessRecords();
    }

    @GetMapping("{id}")
    public DoorAccessRecordDTO getDoorLogById(@PathVariable("id") Integer id) {
        return doorLogService.getDoorLogById(id);
    }
}
