package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.SuggestionDTO;
import com.team2.csmis_api.service.SuggestionService;
import com.team2.csmis_api.service.UserHasSuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private UserHasSuggestionService userHasSuggestionService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/unseen/{userId}")
    public List<SuggestionDTO> getUnseenSuggestionsByUserId(@PathVariable Integer userId) {
        return userHasSuggestionService.findUnseenSuggestionsByUserId(userId);
    }


    @PostMapping
    public ResponseEntity<SuggestionDTO> createSuggestion(@RequestBody SuggestionDTO suggestionDTO) {
        SuggestionDTO createdSuggestion = suggestionService.createSuggestion(suggestionDTO);
        return ResponseEntity.ok(createdSuggestion);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SuggestionDTO>> getAllSuggestions() {
        List<SuggestionDTO> suggestions = suggestionService.getAllSuggestions();
        return ResponseEntity.ok(suggestions);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SuggestionDTO> getSuggestionById(@PathVariable Integer id) {
        SuggestionDTO suggestion = suggestionService.getSuggestionById(id);
        return ResponseEntity.ok(suggestion);
    }

    @PreAuthorize("hasAuthority('OPERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<SuggestionDTO> updateSuggestion(@PathVariable Integer id, @RequestBody SuggestionDTO suggestionDTO) {
        SuggestionDTO updatedSuggestion = suggestionService.updateSuggestion(id, suggestionDTO);
        return ResponseEntity.ok(updatedSuggestion);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable Integer id) {
        suggestionService.deleteSuggestion(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/noti/{id}")
    public ResponseEntity<Void> deleteUserHasSuggestion(@PathVariable Integer id) {
        if (userHasSuggestionService.findById(id).isPresent()) {
            userHasSuggestionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
