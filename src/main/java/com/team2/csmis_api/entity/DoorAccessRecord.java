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

    @ManyToOne
    @JoinColumn(name="location_id")
    private Location location_id ;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user_id ;

}
