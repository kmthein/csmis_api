package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class DoorAccessRecordDTO {
    private int id;
    private String name;
    private String doorLogNo;
    private String location;
    private LocalDateTime dateTime;
    private String status;


}
