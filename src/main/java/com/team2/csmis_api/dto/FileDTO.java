package com.team2.csmis_api.dto;

import com.team2.csmis_api.entity.Base;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FileDTO extends Base {
    private Integer id;
    private String filePath;
    private String type;
}
