package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.SuggestionDTO;
import com.team2.csmis_api.dto.SuggestionNotificationDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.repository.SuggestionRepository;
import com.team2.csmis_api.repository.UserHasSuggestionRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserHasSuggestionRepository userHasSuggestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotificationService notificationService;

    public SuggestionDTO createSuggestion(SuggestionDTO suggestionDTO) {
        // Map the DTO to the Suggestion entity
        Suggestion suggestion = modelMapper.map(suggestionDTO, Suggestion.class);

        // Retrieve the user associated with the suggestion
        User user = userRepository.findById(suggestionDTO.getUserId()).orElse(null);
        suggestion.setUser(user);

        // Save the suggestion
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);

        // Retrieve the list of admins
        List<User> admins = userRepository.findByRole(Role.ADMIN);


        for (User admin : admins) {
            // Create UserHasSuggestion entity for each admin
            UserHasSuggestion userHasSuggestion = new UserHasSuggestion();
            userHasSuggestion.setUser(admin);
            userHasSuggestion.setSuggestion(savedSuggestion);
            userHasSuggestion.setIsSeen(false);
            userHasSuggestionRepository.save(userHasSuggestion);

        }

        String message = "has sent a new suggestion";
        notificationService.sendSuggestionNotification(savedSuggestion.getUser().getName(), message);

        // Return the saved suggestion as a DTO
        return modelMapper.map(savedSuggestion, SuggestionDTO.class);
    }


    public List<SuggestionDTO> getAllSuggestions() {
        List<Suggestion> suggestions = suggestionRepository.getAllSuggestions();

        // Manually map each Suggestion entity to a SuggestionDTO
        return suggestions.stream().map(suggestion -> {
            SuggestionDTO dto = new SuggestionDTO();
            dto.setId(suggestion.getId());
            dto.setDate(suggestion.getDate());
            dto.setContent(suggestion.getContent());
            dto.setUserId(suggestion.getUser() != null ? suggestion.getUser().getId() : null);
            dto.setName(suggestion.getUser() != null ? suggestion.getUser().getName() : null);
            dto.setCreatedAt(suggestion.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public SuggestionDTO getSuggestionByIdAndMakeSeen(Integer id, Integer userId) {
        Suggestion suggestion = suggestionRepository.findById(id).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        UserHasSuggestion userSuggestion = userHasSuggestionRepository.findBySuggestionAndUser(suggestion, user);
        SuggestionDTO suggestionDTO = new SuggestionDTO();
        if(userSuggestion != null) {
            userSuggestion.setIsSeen(true);
            userSuggestion = userHasSuggestionRepository.save(userSuggestion);
            suggestionDTO = convertToSuggestionDto(suggestion);
            suggestionDTO.setSeen(userSuggestion.getIsSeen());
        }
        return suggestion != null ? suggestionDTO : null;
    }

    private SuggestionDTO convertToSuggestionDto(Suggestion suggestion) {
        SuggestionDTO dto = new SuggestionDTO();
        dto.setId(suggestion.getId());
        dto.setCreatedAt(suggestion.getCreatedAt());
        System.out.println(suggestion.getUser().getName());
        System.out.println(suggestion.getUser().getEmail());
        dto.setName(suggestion.getUser().getName());
        dto.setContent(suggestion.getContent());
        dto.setUserId(suggestion.getUser().getId());
        dto.setDate(suggestion.getDate());
        if(suggestion.getUser().getImages().size() > 0) {
            for (FileData file : suggestion.getUser().getImages()) {
                if(!file.getIsDeleted()) {
                    dto.setUserImg(file.getFilePath());
                }
            }
        }
        return dto;
    }

    public SuggestionDTO getSuggestionById(Integer id) {
        Suggestion suggestion = suggestionRepository.findById(id).orElse(null);
        SuggestionDTO suggestionDTO = convertToSuggestionDto(suggestion);
        return suggestion != null ? suggestionDTO : null;
    }

    public SuggestionDTO updateSuggestion(Integer id, SuggestionDTO suggestionDTO) {
        Suggestion existingSuggestion = suggestionRepository.findById(id).orElse(null);
        if (existingSuggestion != null) {
            existingSuggestion.setDate(suggestionDTO.getDate());
            existingSuggestion.setContent(suggestionDTO.getContent());

            // Update user if userId is provided
            if (suggestionDTO.getUserId() != null) {
                User user = userRepository.findById(suggestionDTO.getUserId()).orElse(null);
                existingSuggestion.setUser(user);
            }

            // Save the updated entity
            Suggestion updatedSuggestion = suggestionRepository.save(existingSuggestion);
            return modelMapper.map(updatedSuggestion, SuggestionDTO.class);
        }
        return null; // Handle not found case as needed
    }

    public void deleteSuggestion(Integer id) {

        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found"));
        suggestion.setIsDeleted(true); // Logical deletion
        suggestionRepository.save(suggestion);
    }
}
