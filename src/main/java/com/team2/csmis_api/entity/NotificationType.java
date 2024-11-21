package com.team2.csmis_api.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum NotificationType {

    @Enumerated(EnumType.STRING)
    SUGGESTION,

    @Enumerated(EnumType.STRING)
    ANNOUNCEMENT

}

