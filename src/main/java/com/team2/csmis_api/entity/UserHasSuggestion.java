package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "user_has_suggestion")
@Entity
public class UserHasSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "is_seen", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSeen = false;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="suggestion_id")
    private Suggestion suggestion;

}
