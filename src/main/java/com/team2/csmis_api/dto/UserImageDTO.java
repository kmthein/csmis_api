package com.team2.csmis_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserImageDTO {
    private int userId;
    private String imgUrl;
    private List<Integer> deletedIds;
}
