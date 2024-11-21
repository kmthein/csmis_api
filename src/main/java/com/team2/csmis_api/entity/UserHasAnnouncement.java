package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "user_has_announcement")
@Entity
public class UserHasAnnouncement {

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
    @JoinColumn(name="announcement_id")
    private Announcement announcement;

}
