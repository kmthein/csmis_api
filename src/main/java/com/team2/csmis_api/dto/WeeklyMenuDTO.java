package com.team2.csmis_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyMenuDTO {
    private double price;
    private int rate;
    private String restaurant;
    private List<MenuDTO> menuList;
    private int adminId;
}
