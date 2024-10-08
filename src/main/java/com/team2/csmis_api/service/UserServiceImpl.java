package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.repository.DepartmentRepository;
import com.team2.csmis_api.repository.DivisionRepository;
import com.team2.csmis_api.repository.TeamRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ExcelService excelService;

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DivisionRepository divisionRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ExcelForUserService excelForUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "DAT110ct2";

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }

    @Transactional
    @Override
    public List<User> saveUserToDatabase(MultipartFile file, Integer adminId){
        List<User> users = new ArrayList<>();
        if(ExcelForHolidayService.isValidExcelFile(file)){
            try {
                users = excelForUserService.getUsersDataFromExcel(file.getInputStream());
                String defaultPassword = getDefaultPassword();
                for(User user: users) {
                    user.setRole(Role.OPERATOR);
                    user.setPassword(passwordEncoder.encode(defaultPassword));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        }
        userRepo.saveAll(users);
        return users;
    }

    public UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        if(user.getDivision() != null) {
            userDTO.setDivision(user.getDivision().getName());
        }
        if(user.getDepartment() != null) {
            userDTO.setDepartment(user.getDepartment().getName());
        }
        if(user.getTeam() != null) {
            userDTO.setTeam(user.getTeam().getName());
        }
        if(user.getIsActive() == true) {
            userDTO.setStatus(Status.Active);
        } else {
            userDTO.setStatus(Status.InActive);
        }
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
