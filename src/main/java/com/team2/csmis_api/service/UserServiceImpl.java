package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.exception.ResourceNotFoundException;
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
import java.util.Objects;

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

    @Override
    public ResponseDTO updateUserById(UserDTO userDTO, int id) {
        User tempUser = userRepo.findById(id).get();
        ResponseDTO res = new ResponseDTO();
        if(tempUser == null) {
            throw new ResourceNotFoundException("Staff not existed with this id.");
        }
        tempUser.setName(userDTO.getName());
        tempUser.setStaffId(userDTO.getStaffId());
        tempUser.setDoorLogNo(Integer.valueOf(userDTO.getDoorLogNo()));
        tempUser.setEmail(userDTO.getEmail());
        if(tempUser.getIsActive() == userDTO.getStatus().equals("Active")) {
            tempUser.setIsActive(true);
        } else {
            tempUser.setIsActive(false);
        }
        if(tempUser.getIsVegan() != userDTO.getIsVegan()) {
            tempUser.setIsVegan(userDTO.getIsVegan());
        }
        if(!Objects.equals(tempUser.getRole().toString(), userDTO.getRole().toString())) {
            tempUser.setRole(userDTO.getRole());
        };
        Division tempDivision = divisionRepo.findDivisionByName(userDTO.getDivision());
        if(tempDivision != null && Objects.equals(tempDivision.getName(), userDTO.getDivision())) {
            tempUser.setDivision(tempDivision);
        } else if(tempDivision == null) {
            tempDivision = new Division();
            tempDivision.setName(userDTO.getDivision());
            divisionRepo.save(tempDivision);
            tempUser.setDivision(tempDivision);
        }
        Department tempDepart = departmentRepo.findDepartmentByName(userDTO.getDepartment());
        if(tempDepart != null && Objects.equals(tempDepart.getName(), userDTO.getDepartment())) {
            tempUser.setDepartment(tempDepart);
        } else if(tempDepart == null) {
            tempDepart = new Department();
            tempDepart.setName(userDTO.getDepartment());
            departmentRepo.save(tempDepart);
            tempUser.setDepartment(tempDepart);
        }
        Team tempTeam = teamRepo.findTeamByName(userDTO.getTeam());
        if(tempTeam != null && Objects.equals(tempTeam.getName(), userDTO.getTeam())) {
            tempUser.setTeam(tempTeam);
        } else if(tempTeam == null) {
            tempTeam = new Team();
            tempTeam.setName(userDTO.getTeam());
            teamRepo.save(tempTeam);
            tempUser.setTeam(tempTeam);
        }
        User userSave = userRepo.save(tempUser);
        if(userSave != null) {
            res.setStatus("200");
            res.setMessage("Staff data updated successfully.");
        } else {
            res.setStatus("403");
            res.setMessage("Staff data update failed.");
        }
        return res;
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
            userDTO.setStatus(Status.Active.toString());
        } else {
            userDTO.setStatus(Status.InActive.toString());
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
    public ResponseDTO addNewUser(UserDTO userDTO) {
        ResponseDTO res = new ResponseDTO();
        User staffExist = userRepo.findByStaffId(userDTO.getStaffId());
        if(staffExist != null) {
            res.setStatus("409");
            res.setMessage("Staff already existed with this id.");
            return res;
        }
        User tempUser = mapper.map(userDTO, User.class);
        Division tempDivision = divisionRepo.findDivisionByName(userDTO.getDivision());
        if(tempDivision != null && Objects.equals(tempDivision.getName(), userDTO.getDivision())) {
            tempUser.setDivision(tempDivision);
        } else if(tempDivision == null) {
            tempDivision = new Division();
            tempDivision.setName(userDTO.getDivision());
            divisionRepo.save(tempDivision);
            tempUser.setDivision(tempDivision);
        }
        Department tempDepart = departmentRepo.findDepartmentByName(userDTO.getDepartment());
        if(tempDepart != null && Objects.equals(tempDepart.getName(), userDTO.getDepartment())) {
            tempUser.setDepartment(tempDepart);
        } else if(tempDepart == null) {
            tempDepart = new Department();
            tempDepart.setName(userDTO.getDepartment());
            departmentRepo.save(tempDepart);
            tempUser.setDepartment(tempDepart);
        }
        Team tempTeam = teamRepo.findTeamByName(userDTO.getTeam());
        if(tempTeam != null && Objects.equals(tempTeam.getName(), userDTO.getTeam())) {
            tempUser.setTeam(tempTeam);
        } else if(tempTeam == null) {
            tempTeam = new Team();
            tempTeam.setName(userDTO.getTeam());
            teamRepo.save(tempTeam);
            tempUser.setTeam(tempTeam);
        }
        if(userDTO.getStatus().equals("Active")) {
            tempUser.setIsActive(true);
        } else {
            tempUser.setIsActive(false);
        }
        tempUser.setIsVegan(false);
        String defaultPassword = getDefaultPassword();
        tempUser.setPassword(passwordEncoder.encode(defaultPassword));
        User userSave = userRepo.save(tempUser);
        if(userSave != null) {
            res.setStatus("200");
            res.setMessage("Staff data insert successfully.");
        } else {
            res.setStatus("403");
            res.setMessage("Staff data insert failed.");
        }
        return res;
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = userRepo.getUserById(id);
        UserDTO userDTO = mapUserToDTO(user);
        return userDTO;
    }
}
