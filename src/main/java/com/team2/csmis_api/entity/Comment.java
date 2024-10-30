//package com.team2.csmis_api.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Data
//@Table(name = "comment")
//@Entity
//public class Comment extends Base{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Integer id;
//
//    @Column(name = "content")
//    private String content;
//
//    @Column(name = "date")
//    private LocalDate date;
//
//    @ManyToOne
//    @JoinColumn(name="user_id")
//    private User user;
//
//    @ManyToMany(mappedBy = "comments")
//    private List<Announcement> announcements;
//
//}
