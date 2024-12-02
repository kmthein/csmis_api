package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.MeatDTO;
import com.team2.csmis_api.entity.Meat;
import com.team2.csmis_api.service.MeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meat")
public class MeatController {
    @Autowired
    private MeatService meatService;

    @GetMapping
    public ResponseEntity<List<Meat>> getAllMeats() {
        List<Meat> meats = meatService.findAll();
        return ResponseEntity.ok(meats);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Meat> getMeatById(@PathVariable Integer id) {
        return meatService.findById(id)
                .map(meat -> new ResponseEntity<>(meat, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Meat> createMeat(@RequestBody Meat meat) {
        if (!meatService.isNameUnique(meat.getName())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
        }
        Meat savedMeat = meatService.save(meat);
        return new ResponseEntity<>(savedMeat, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeat(@PathVariable Integer id, @RequestBody Meat meat) {
        meat.setId(id);

        if (!meatService.isNameUnique(meat.getName())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("A meat with the name '" + meat.getName() + "' already exists.");
        }

        Meat updatedMeat = meatService.save(meat);
        return new ResponseEntity<>(updatedMeat, HttpStatus.OK);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteMeat(@PathVariable Integer id) {
        meatService.softDeleteMeat(id);
        return ResponseEntity.noContent().build();
    }
}
