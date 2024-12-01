package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.FeedbackResponse;
import com.team2.csmis_api.repository.FeedbackResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackResponseService {

    @Autowired
    private FeedbackResponseRepository feedbackResponseRepository;

    // Create FeedbackResponse
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse) {
        return feedbackResponseRepository.save(feedbackResponse);
    }

    // Update FeedbackResponse
    public FeedbackResponse updateFeedbackResponse(Integer id, FeedbackResponse feedbackResponse) {
        Optional<FeedbackResponse> existingResponse = feedbackResponseRepository.findById(id);

        if (existingResponse.isPresent()) {
            FeedbackResponse updatedResponse = existingResponse.get();
            updatedResponse.setResponse(feedbackResponse.getResponse());
            return feedbackResponseRepository.save(updatedResponse);
        }

        return null; // Handle this as needed, e.g., throw exception
    }

    // Delete FeedbackResponse
    public boolean deleteFeedbackResponse(Integer id) {
        if (feedbackResponseRepository.existsById(id)) {
            feedbackResponseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get All FeedbackResponses
    public List<FeedbackResponse> getAllFeedbackResponses() {
        return feedbackResponseRepository.findAll();
    }

    // Get FeedbackResponse by ID
    public FeedbackResponse getFeedbackResponseById(Integer id) {
        return feedbackResponseRepository.findById(id).orElse(null);
    }
}
