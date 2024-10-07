package com.team2.csmis_api.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String staff_id;
    private String door_log_no;
    private Boolean isActive;
    private Boolean isVegan;
    private String role;
    private String email;

}
