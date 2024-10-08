package com.team2.csmis_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team2.csmis_api.entity.Role;
import com.team2.csmis_api.entity.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private int id;
    private String name;
    private String staffId;
    private Role role;
    private String doorLogNo;
    private Boolean isVegan;
    private String division;
    private String department;
    private String team;
    private Status status;
}
