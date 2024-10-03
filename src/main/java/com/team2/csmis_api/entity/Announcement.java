package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Table(name = "announcement")
@Entity
public class Announcement extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name="admin_id")
    private User admin_id;

    @ManyToMany
    @JoinTable(
            name = "announcement_has_comment",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private List<Comment> comments;

}
