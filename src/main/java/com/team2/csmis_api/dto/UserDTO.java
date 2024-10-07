package com.team2.csmis_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team2.csmis_api.entity.Role;
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
}
