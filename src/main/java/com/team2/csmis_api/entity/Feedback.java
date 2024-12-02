package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "feedback", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "lunch_id"})
})
@Entity
public class Feedback extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name="lunch_id")
    private Lunch lunch;

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_given", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isGiven;

    @ManyToOne
    @JoinColumn(name="feedback_response_id")
    private FeedbackResponse response;
}
