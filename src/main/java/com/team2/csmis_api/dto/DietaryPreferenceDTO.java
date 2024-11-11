package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaryPreferenceDTO {
    private Integer userId;
    private Boolean isVegan;
    private List<Integer> meatIds;
}
