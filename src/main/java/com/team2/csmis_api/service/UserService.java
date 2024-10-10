package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllActiveUsers();

    ResponseDTO addNewUser(UserDTO userDTO);

    UserDTO getUserById(int id);

    List<User> saveUserToDatabase(MultipartFile file, Integer adminId) throws IOException;

    ResponseDTO updateUserById(UserDTO userDTO, int id);
}
