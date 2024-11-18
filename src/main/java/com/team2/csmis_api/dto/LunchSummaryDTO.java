package com.team2.csmis_api.dto;

import lombok.Data;


public interface LunchSummaryDTO {
    Integer getRegisterAndEat();
    Integer getRegisterNotEat();
    Integer getUnregisterButEat();
}
