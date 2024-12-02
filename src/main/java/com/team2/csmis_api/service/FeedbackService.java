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
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public FeedbackDTO getFeedbackByUserAndDate(Integer userId, LocalDate date) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findByUserIdAndDate(userId, date);
        if (feedbackOptional.isEmpty()) {
            return null; // Or throw an exception based on your requirements
        }

        Feedback feedback = feedbackOptional.get();
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUser().getId());
        dto.setResponseId(feedback.getResponse().getId());
        dto.setLunchId(feedback.getLunch().getId());
        dto.setDate(feedback.getDate());
        dto.setComment(feedback.getComment());
        dto.setUserName(feedback.getUser().getName()); // Assuming user has a getName() method
        dto.setLunchMenu(feedback.getLunch().getMenu()); // Assuming lunch has a getMenu() method

        return dto;
    }

    public long getFeedbackCountByResponseId(Integer responseId) {
        return feedbackRepository.countFeedbacksByResponseId(responseId);
    }


    public boolean hasGivenFeedback(Long userId, Long lunchId) {
        return feedbackRepository.existsByUserIdAndLunchId(userId, lunchId);
    }

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

    @Transactional
    public FeedbackDTO updateFeedback(Integer id, FeedbackDTO feedbackDTO) {
        System.out.println("Updating feedback with ID: " + id);
        System.out.println("Provided responseId: " + feedbackDTO.getResponseId());

        // Find the existing Feedback
        Feedback existingFeedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found for ID: " + id));

        // Update the comment field
        existingFeedback.setComment(feedbackDTO.getComment());

        // Update the responseId if provided
        if (feedbackDTO.getResponseId() != null) {
            FeedbackResponse response = feedbackResponseRepository.findById(feedbackDTO.getResponseId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "FeedbackResponse not found for ID: " + feedbackDTO.getResponseId()
                    ));
            existingFeedback.setResponse(response);
        } else {
            System.out.println("No responseId provided; skipping response update.");
        }

        // Save the updated feedback entity
        Feedback updatedFeedback = feedbackRepository.save(existingFeedback);

        // Map updated entity back to DTO
        FeedbackDTO updatedFeedbackDTO = new FeedbackDTO();
        updatedFeedbackDTO.setId(updatedFeedback.getId());
        updatedFeedbackDTO.setComment(updatedFeedback.getComment());
        updatedFeedbackDTO.setResponseId(updatedFeedback.getResponse() != null ? updatedFeedback.getResponse().getId() : null);

        System.out.println("Feedback successfully updated: " + updatedFeedbackDTO);
        return updatedFeedbackDTO;
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
