package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedbackDTO {
    private Integer id;
    private Integer userId;
    private Integer lunchId;
    private LocalDate date;
    private String comment;
    private String response;
}
