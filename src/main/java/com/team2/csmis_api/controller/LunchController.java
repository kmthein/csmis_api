package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.LunchDTO;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.service.LunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lunches")
public class LunchController {

    @Autowired
    private LunchService lunchService;

    @GetMapping("")
    public ResponseEntity<List<LunchDTO>> getAllLunches() {
        try {
            List<LunchDTO> lunches = lunchService.findAll();
            return ResponseEntity.ok(lunches);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("")
    public ResponseEntity<LunchDTO> createLunch(@RequestBody LunchDTO lunchDTO) {
        LunchDTO createdLunchDTO = lunchService.save(lunchDTO);
        return ResponseEntity.ok(createdLunchDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LunchDTO> updateLunch(@PathVariable Integer id, @RequestBody LunchDTO lunchDTO) {
        LunchDTO updatedLunch = lunchService.updateLunch(id, lunchDTO);
        return ResponseEntity.ok(updatedLunch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLunch(@PathVariable Integer id) {
        lunchService.deleteLunch(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
