package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuggestionNotificationDTO {

    private String username;
    private Boolean isSeen;
}
