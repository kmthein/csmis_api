package com.team2.csmis_api.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Role {
    @Enumerated(EnumType.STRING)
    NONE,
    @Enumerated(EnumType.STRING)
    OPERATOR,
    @Enumerated(EnumType.STRING)
    ADMIN
}
