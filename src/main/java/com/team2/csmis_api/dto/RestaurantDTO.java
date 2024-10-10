package com.team2.csmis_api.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private Integer id;
    private String name;
    private String address;
    private String contact;
    private String email;
}
