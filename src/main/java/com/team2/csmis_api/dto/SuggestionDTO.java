package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SuggestionDTO {

    private Integer id;
    private LocalDate date;
    private String content;
    private Integer userId;
    private boolean isSeen;
    private String username;

}
