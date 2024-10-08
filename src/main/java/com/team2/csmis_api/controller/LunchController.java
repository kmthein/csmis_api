package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.LunchDTO;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.service.LunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lunches")
public class LunchController {

    @Autowired
    private LunchService lunchService;

    @GetMapping
    public List<Lunch> getAllLunches() {
        return lunchService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lunch> getLunchById(@PathVariable Integer id) {
        return lunchService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Lunch createLunch(@RequestBody LunchDTO lunchDTO) {
        return lunchService.create(lunchDTO);
    }

    @PutMapping("/{id}")
    public Lunch updateLunch(@PathVariable Integer id, @RequestBody LunchDTO lunchDTO) {
        return lunchService.update(id, lunchDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLunch(@PathVariable Integer id) {
        lunchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
