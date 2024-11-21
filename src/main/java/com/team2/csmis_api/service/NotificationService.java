package com.team2.csmis_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.csmis_api.dto.SuggestionNotificationDTO;
import com.team2.csmis_api.entity.UserHasSuggestion;
import com.team2.csmis_api.repository.UserHasSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserHasSuggestionRepository userHasSuggestionRepository;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendFeedbackNotification(String message) {
        messagingTemplate.convertAndSend("/topic/feedbacks", message);
    }

    public void sendSuggestionNotification(String userName, String message) {
        String notificationMessage = String.format("{\"message\": \"%s\", \"userName\": \"%s\"}", message, userName);
        messagingTemplate.convertAndSend("/topic/suggestions", notificationMessage);
    }


    public void sendAnnouncementNotification(String message) {
        messagingTemplate.convertAndSend("/topic/announcements", message);
    }
}
