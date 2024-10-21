package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedbackDTO {
    private Integer id;
    private String title;
    private LocalDate date;
    private String content;
    private Integer userId;
}
