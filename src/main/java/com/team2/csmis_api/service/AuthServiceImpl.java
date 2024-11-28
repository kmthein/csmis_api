package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.ResponseTokenDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.FileData;
import com.team2.csmis_api.entity.Role;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private JwtService jwtService;

    @Override
    public ResponseDTO register(User user) {
        ResponseDTO res = new ResponseDTO();
        User staffIdExist = userRepo.findByStaffId(user.getStaffId());
        if(staffIdExist != null) {

        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.OPERATOR);
            userRepo.save(user);
            res.setStatus("201");
            res.setMessage("User Registration Successful");
        }
        return res;
    }

    @Override
    public Object login(User user) {
        ResponseDTO res = new ResponseDTO();
        try {
            User existUser = userRepo.findByStaffId(user.getStaffId());
            if(existUser == null) {
                res.setStatus("404");
                res.setMessage("Staff ID is not found, try again");
                return res;
            }
            if(existUser.getIsActive() == false) {
                res.setStatus("403");
                res.setMessage("This Staff ID can't access to the system, try another");
                return res;
            }
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getStaffId(), user.getPassword()));
            String token = jwtService.generateToken(existUser);
            ResponseTokenDTO tokenDTO = new ResponseTokenDTO();
            tokenDTO.setToken(token);
            UserDTO userDTO = mapper.map(existUser, UserDTO.class);
            if(existUser.getImages().size() > 0) {
                for(FileData img: existUser.getImages()) {
                    if(!img.getIsDeleted()) {
                        userDTO.setImgUrl(img.getFilePath());
                    }
                }
            }
            tokenDTO.setUserDetails(userDTO);
            return tokenDTO;
        } catch (BadCredentialsException exception) {
            res.setStatus("401");
            res.setMessage("Password is incorrect, try again");
            return res;
        } catch (UsernameNotFoundException exception) {
            res.setStatus("404");
            res.setMessage("An error occurred during login");
            return res;
        }
    }
}
