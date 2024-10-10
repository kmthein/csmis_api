package com.team2.csmis_api.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LunchRegistrationDTO {
    private Integer userId;
    private List<Date> selectedDates;

}

