package com.team2.csmis_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseTokenDTO {
    private String token;
    private UserDTO userDetails;

    public ResponseTokenDTO() {
    }

    public ResponseTokenDTO(String token) {
        this.token = token;
    }
}
