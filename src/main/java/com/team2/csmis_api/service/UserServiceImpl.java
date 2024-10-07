package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper mapper;

    public UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        return userDTO;
    }

    @Override
    public List<UserDTO> getAllActiveUsers() {
        List<User> users = userRepo.getAllActiveUsers();
        List<UserDTO> userDTOList = new ArrayList<>();

        for(User user: users) {
            UserDTO userDTO = mapUserToDTO(user);
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = userRepo.getUserById(id);
        UserDTO userDTO = mapUserToDTO(user);
        return userDTO;
    }
}
