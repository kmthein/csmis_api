package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Table(name = "door_access_record")
@Entity
public class DoorAccessRecord extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "department")
    private String department;

    @Column(name = "door_log_no")
    private Integer doorLogNo;

    @ManyToOne
    @JoinColumn(name="location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name="admin_id")
    private User admin;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
