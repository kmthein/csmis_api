package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvoidMeatDTO {
    private String meat;
    private String day;
    private Long count;
}
