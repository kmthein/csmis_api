package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.HolidayDTO;
import com.team2.csmis_api.entity.Holiday;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @PostMapping("")
    public ResponseEntity<?> uploadHolidaysData(@RequestParam("file") MultipartFile file,
                                                @RequestParam("adminId") User adminId) {
        Holiday holiday = new Holiday();
        holiday.setUser(adminId);
        holidayService.saveHolidayToDatabase(file, holiday);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("Message", "Holidays data uploaded and saved to database successfully"));
    }

    @GetMapping("")
    public List<HolidayDTO> showAllHoliday() {
        return holidayService.showAllHoliday();
    }

    @GetMapping("{id}")
    public ResponseEntity<HolidayDTO> showById(@PathVariable("id") Integer id) {
        return holidayService.showByHolidayId(id)
                .map(holidayDTO -> ResponseEntity.ok(holidayDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HolidayDTO> updateHoliday(@PathVariable("id") Integer id, @RequestBody HolidayDTO holidayDTO) {
        HolidayDTO updatedHoliday = holidayService.updateHoliday(id, holidayDTO);
        return ResponseEntity.ok(updatedHoliday);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable("id") Integer id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}
