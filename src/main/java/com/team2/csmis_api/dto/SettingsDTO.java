package com.team2.csmis_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@ToString
public class SettingsDTO {
    private String lastRegisterDay;
    private String lastRegisterTime;
    private Double companyRate;
    private Double currentLunchPrice;
    private String lunchReminderTime;
    private Integer adminId;
}
