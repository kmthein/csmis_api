package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.FeedbackDTO;
import com.team2.csmis_api.entity.Feedback;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.FeedbackRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);
        User user = userRepository.findById(feedbackDTO.getUserId()).orElse(null);
        feedback.setUser(user);
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return modelMapper.map(savedFeedback, FeedbackDTO.class);
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                .collect(Collectors.toList());
    }

    public FeedbackDTO getFeedbackById(Integer id) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        return modelMapper.map(feedback, FeedbackDTO.class);
    }

    public FeedbackDTO updateFeedback(Integer id, FeedbackDTO feedbackDTO) {
        Feedback existingFeedback = feedbackRepository.findById(id).orElse(null);
        if (existingFeedback != null) {
            // Manually update properties from DTO to entity
            existingFeedback.setTitle(feedbackDTO.getTitle());
            existingFeedback.setDate(feedbackDTO.getDate());
            existingFeedback.setContent(feedbackDTO.getContent());

            // Update user if userId is provided
            if (feedbackDTO.getUserId() != null) {
                User user = userRepository.findById(feedbackDTO.getUserId()).orElse(null);
                existingFeedback.setUser(user);
            }

            // Save the updated entity
            Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
            return modelMapper.map(updatedFeedback, FeedbackDTO.class); // Return updated DTO
        }
        return null; // Handle not found case as needed
    }


    public void deleteFeedback(Integer id) {
        feedbackRepository.deleteFeedback(id); // Changed to use deleteById
    }
}
