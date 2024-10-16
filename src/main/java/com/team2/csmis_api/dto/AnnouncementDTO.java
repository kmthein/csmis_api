package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AnnouncementDTO {
    private Integer id;
    private String title;
    private LocalDate date;
    private String content;
    private Integer adminId;
    private List<Integer> fileIds;

}
