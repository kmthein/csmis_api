package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserActionDTO {
    private String name;
    private Integer doorLogNo;
    private LocalDate date;
    private Integer userId;
    private Integer Id;

}
