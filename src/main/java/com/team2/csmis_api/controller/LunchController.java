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

    @GetMapping
    public ResponseEntity<List<Lunch>> getAllLunches() {
        List<Lunch> lunches = lunchService.getAllLunches();
        return new ResponseEntity<>(lunches, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lunch> getLunchById(@PathVariable Integer id) {
        return lunchService.getLunchById(id)
                .map(lunch -> new ResponseEntity<>(lunch, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Lunch> createLunch(@RequestBody LunchDTO lunchDTO) {
        Lunch createdLunch = lunchService.createLunch(lunchDTO);
        return new ResponseEntity<>(createdLunch, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lunch> updateLunch(@PathVariable Integer id, @RequestBody LunchDTO lunchDTO) {
        Lunch updatedLunch = lunchService.updateLunch(id, lunchDTO);
        return new ResponseEntity<>(updatedLunch, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLunch(@PathVariable Integer id) {
        lunchService.deleteLunch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
