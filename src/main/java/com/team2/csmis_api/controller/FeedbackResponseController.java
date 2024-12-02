package com.team2.csmis_api.controller;

import com.team2.csmis_api.entity.FeedbackResponse;
import com.team2.csmis_api.service.FeedbackResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/api/feedback-response")
public class FeedbackResponseController {

    @Autowired
    private FeedbackResponseService feedbackResponseService;

    // Create FeedbackResponse
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedbackResponse(@RequestBody FeedbackResponse feedbackResponse) {
        FeedbackResponse createdResponse = feedbackResponseService.createFeedbackResponse(feedbackResponse);
        return ResponseEntity.ok(createdResponse);
    }

    // Update FeedbackResponse
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedbackResponse(@PathVariable Integer id, @RequestBody FeedbackResponse feedbackResponse) {
        FeedbackResponse updatedResponse = feedbackResponseService.updateFeedbackResponse(id, feedbackResponse);
        if (updatedResponse != null) {
            return ResponseEntity.ok(updatedResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete FeedbackResponse
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedbackResponse(@PathVariable Integer id) {
        boolean deleted = feedbackResponseService.deleteFeedbackResponse(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get All FeedbackResponses
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbackResponses() {
        List<FeedbackResponse> feedbackResponses = feedbackResponseService.getAllFeedbackResponses();
        return ResponseEntity.ok(feedbackResponses);
    }

    // Get FeedbackResponse by ID
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackResponseById(@PathVariable Integer id) {
        FeedbackResponse feedbackResponse = feedbackResponseService.getFeedbackResponseById(id);
        if (feedbackResponse != null) {
            return ResponseEntity.ok(feedbackResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
