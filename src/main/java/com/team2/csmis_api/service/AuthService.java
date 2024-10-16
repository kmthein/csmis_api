package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.entity.User;

public interface AuthService {
    ResponseDTO register(User user);

    Object login(User user);
}
