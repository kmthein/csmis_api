package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "file")
@Entity
public class FileData extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "file_path",columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "file_type")
    private String fileType;

    @ManyToMany(mappedBy = "fileData")
    private List<Announcement> announcements;

}