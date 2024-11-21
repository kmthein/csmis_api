package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.FeedbackDTO;
import com.team2.csmis_api.entity.Feedback;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Response;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.FeedbackRepository;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ModelMapper modelMapper;

    // Create Feedback
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);

        User user = userRepository.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Lunch lunch = lunchRepository.findById(feedbackDTO.getLunchId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid lunch ID"));
        feedback.setUser(user);
        feedback.setLunch(lunch);
        feedback.setDate(LocalDate.now());

        // Automatically sets createdAt and updatedAt in Base
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return modelMapper.map(savedFeedback, FeedbackDTO.class);
    }

    public FeedbackDTO updateFeedback(Integer id, FeedbackDTO feedbackDTO) {
        // Fetch existing feedback from the database
        Feedback existingFeedback = feedbackRepository.findById(id)
                .filter(f -> !f.getIsDeleted()) // Ensure it's not deleted
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));

        // Update fields
        existingFeedback.setComment(feedbackDTO.getComment());
        existingFeedback.setResponse(Response.valueOf(feedbackDTO.getResponse()));

        // If user or lunch is being updated, set them again
        if (feedbackDTO.getUserId() != null) {
            User user = userRepository.findById(feedbackDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
            existingFeedback.setUser(user);
        }

        if (feedbackDTO.getLunchId() != null) {
            Lunch lunch = lunchRepository.findById(feedbackDTO.getLunchId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid lunch ID"));
            existingFeedback.setLunch(lunch);
        }

        // Optionally, update the date if required (for example, to set a new date on updates)
        existingFeedback.setDate(LocalDate.now()); // You can choose whether to update the date or not.

        // Save and return the updated Feedback
        Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
        return modelMapper.map(updatedFeedback, FeedbackDTO.class);
    }

    // Logical Delete Feedback
    public void deleteFeedback(Integer id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        feedback.setIsDeleted(true); // Logical deletion
        feedbackRepository.save(feedback);
    }

    // Get All Feedbacks
    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAllActiveFeedbacks()
                .stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                .collect(Collectors.toList());
    }

    // Get Feedback by ID
    public FeedbackDTO getFeedbackById(Integer id) {
        Feedback feedback = feedbackRepository.findById(id)
                .filter(f -> !f.getIsDeleted()) // Exclude deleted feedbacks
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        return modelMapper.map(feedback, FeedbackDTO.class);
    }
}
