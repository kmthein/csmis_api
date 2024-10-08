package com.team2.csmis_api.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Status {
    @Enumerated(EnumType.STRING)
    None,
    @Enumerated(EnumType.STRING)
    Active,
    @Enumerated(EnumType.STRING)
    InActive
}
