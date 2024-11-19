package com.team2.csmis_api.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Response {

    @Enumerated(EnumType.STRING)
    GOOD,
    @Enumerated(EnumType.STRING)
    BAD
}
