package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllActiveUsers();

    UserDTO getUserById(int id);
}
