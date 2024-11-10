package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.SuggestionDTO;
import com.team2.csmis_api.entity.UserHasSuggestion;
import com.team2.csmis_api.repository.UserHasSuggestionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserHasSuggestionService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserHasSuggestionRepository userHasSuggestionRepository;

    public List<SuggestionDTO> getLatestSuggestions() {
        List<UserHasSuggestion> suggestions = userHasSuggestionRepository.findLatestSuggestions();

        // Limit to the latest 6 suggestions
        return suggestions.stream()
                .limit(6)
                .map(this::convertToUserHasSuggestionDto)
                .collect(Collectors.toList());
    }

    public List<SuggestionDTO> findUnseenSuggestionsByUserId(Integer userId) {
        List<UserHasSuggestion> unseenSuggestions = userHasSuggestionRepository.findByIsSeenFalseAndUser_Id(userId);
        return unseenSuggestions.stream()
                .map(this::convertToUserHasSuggestionDto)
                .collect(Collectors.toList());
    }

    private SuggestionDTO convertToUserHasSuggestionDto(UserHasSuggestion userHasSuggestion) {
        SuggestionDTO dto = new SuggestionDTO();
        dto.setId(userHasSuggestion.getSuggestion().getId());
        dto.setDate(userHasSuggestion.getSuggestion().getDate());
        dto.setContent(userHasSuggestion.getSuggestion().getContent());
        dto.setUserId(userHasSuggestion.getUser().getId());
        return dto;
    }

    public List<UserHasSuggestion> findAll() {
        return userHasSuggestionRepository.findAll();
    }

    public Optional<UserHasSuggestion> findById(Integer id) {
        return userHasSuggestionRepository.findById(id);
    }

    public UserHasSuggestion save(UserHasSuggestion userHasSuggestion) {
        return userHasSuggestionRepository.save(userHasSuggestion);
    }

    public void deleteById(Integer id) {
        userHasSuggestionRepository.deleteById(id);
    }
}
