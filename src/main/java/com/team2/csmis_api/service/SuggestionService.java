package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.SuggestionDTO;
import com.team2.csmis_api.entity.Role;
import com.team2.csmis_api.entity.Suggestion;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasSuggestion;
import com.team2.csmis_api.repository.SuggestionRepository;
import com.team2.csmis_api.repository.UserHasSuggestionRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Suggestion suggestion = modelMapper.map(suggestionDTO, Suggestion.class);
        User user = userRepository.findById(suggestionDTO.getUserId()).orElse(null);
        suggestion.setUser(user);
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);

        List<User> admins = userRepository.findByRole(Role.ADMIN);
        for (User admin : admins) {
            UserHasSuggestion userHasSuggestion = new UserHasSuggestion();
            userHasSuggestion.setUser(admin);
            userHasSuggestion.setSuggestion(savedSuggestion);  // Replace savedFeedback with savedSuggestion
            userHasSuggestion.setIsSeen(false);
            userHasSuggestionRepository.save(userHasSuggestion);  // Save the UserHasSuggestion
        }


        // Send notification to admin
        notificationService.sendSuggestionNotification("{\"message\": \"New suggestion received\"}");

        return modelMapper.map(savedSuggestion, SuggestionDTO.class);
    }

    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionRepository.findAll().stream()
                .map(suggestion -> modelMapper.map(suggestion, SuggestionDTO.class))
                .collect(Collectors.toList());
    }

    public SuggestionDTO getSuggestionById(Integer id) {
        Suggestion suggestion = suggestionRepository.findById(id).orElse(null);
        return suggestion != null ? modelMapper.map(suggestion, SuggestionDTO.class) : null;
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
        suggestionRepository.deleteById(id);
    }
}
