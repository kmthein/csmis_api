package com.team2.csmis_api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HolidayDTO{

    private Integer id;
    private String name;
    private LocalDate date;
    private Integer adminId;

}