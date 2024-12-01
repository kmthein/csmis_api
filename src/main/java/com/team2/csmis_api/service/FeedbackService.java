package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.FeedbackDTO;
import com.team2.csmis_api.entity.Feedback;
import com.team2.csmis_api.entity.FeedbackResponse;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.FeedbackRepository;
import com.team2.csmis_api.repository.FeedbackResponseRepository;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LunchRepository lunchRepository;

    @Autowired
    private FeedbackResponseRepository feedbackResponseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotificationService notificationService;

    // Create Feedback
    @Transactional
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        System.out.println("Response ID: " + feedbackDTO.getResponseId()); // Debug log

        Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);

        if (feedbackDTO.getResponseId() != null) {
            feedback.setResponse(feedbackResponseRepository.findById(feedbackDTO.getResponseId()).orElse(null));
        }

        feedback.setUser(userRepository.findById(feedbackDTO.getUserId()).orElse(null));
        feedback.setLunch(lunchRepository.findById(feedbackDTO.getLunchId()).orElse(null));
        feedback.setDate(LocalDate.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        String message = "has sent a feedback the menu on";
        notificationService.sendSuggestionNotification(savedFeedback.getUser().getName(), message);
        return modelMapper.map(savedFeedback, FeedbackDTO.class);
    }

    // Update Feedback
    @Transactional
    public FeedbackDTO updateFeedback(Integer id, FeedbackDTO feedbackDTO) {
        Feedback existingFeedback = feedbackRepository.findById(id)
                .filter(feedback -> !feedback.getIsDeleted())
                .orElse(null);

        if (existingFeedback == null) {
            return null; // Handle as needed
        }

        existingFeedback.setComment(feedbackDTO.getComment());

        if (feedbackDTO.getResponseId() != null) {
            existingFeedback.setResponse(feedbackResponseRepository.findById(feedbackDTO.getResponseId()).orElse(null));
        }

        if (feedbackDTO.getUserId() != null) {
            existingFeedback.setUser(userRepository.findById(feedbackDTO.getUserId()).orElse(null));
        }

        if (feedbackDTO.getLunchId() != null) {
            existingFeedback.setLunch(lunchRepository.findById(feedbackDTO.getLunchId()).orElse(null));
        }

        existingFeedback.setDate(feedbackDTO.getDate() != null ? feedbackDTO.getDate() : existingFeedback.getDate());

        Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
        return modelMapper.map(updatedFeedback, FeedbackDTO.class);
    }

    // Logical Delete Feedback
    @Transactional
    public void deleteFeedback(Integer id) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback != null) {
            feedback.setIsDeleted(true);
            feedbackRepository.save(feedback);
        }
    }

    // Get All Feedbacks
    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAllActiveFeedbacksWithDetails()
                .stream()
                .map(feedback -> {
                    FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);

                    // Map userName
                    if (feedback.getUser() != null) {
                        dto.setUserName(feedback.getUser().getName());
                    }

                    // Map response
                    if (feedback.getResponse() != null) {
                        dto.setResponse(feedback.getResponse().getResponse());
                    }

                    // Map lunch menu
                    if (feedback.getLunch() != null) {
                        dto.setLunchMenu(feedback.getLunch().getMenu());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    // Get Feedback by ID
    public FeedbackDTO getFeedbackById(Integer id) {
        Feedback feedback = feedbackRepository.findById(id)
                .filter(f -> !f.getIsDeleted())
                .orElse(null);
        return feedback != null ? modelMapper.map(feedback, FeedbackDTO.class) : null;
    }
}
