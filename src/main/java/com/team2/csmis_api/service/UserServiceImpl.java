package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.DietaryPreferenceDTO;
import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.*;
import com.team2.csmis_api.repository.DepartmentRepository;
import com.team2.csmis_api.repository.DivisionRepository;
import com.team2.csmis_api.repository.TeamRepository;
import com.team2.csmis_api.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private MeatRepository meatRepo;

    @Autowired
    private EmailService emailService;

    private static final String DEFAULT_PASSWORD = "DAT110ct2";

    @Value("${app.base-url}")
    private String baseUrl;

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }

    @Override
    public ResponseDTO forcePasswordChange(int id, String newPassword) {
        User user = userRepo.getUserById(id);
        ResponseDTO res = new ResponseDTO();
        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setHasDefaultPassword(false);
        User updateUser = userRepo.save(user);
        if(updateUser != null) {
            res.setStatus("200");
            res.setMessage("Password changed successfully");
        } else {
            res.setStatus("403");
            res.setMessage("Password can't be changed");
        }
        return res;
    }

    @Transactional
    @Override
    public List<User> saveUserToDatabase(MultipartFile file, Integer adminId){
        List<User> users = new ArrayList<>();
        if(ExcelForHolidayService.isValidExcelFile(file)){
            try {
                users = excelForUserService.getUsersDataFromExcel(file.getInputStream());
                String defaultPassword = getDefaultPassword();
                String subject = "Welcome to the CSMIS!";
                for(User user: users) {
                    String loginUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                            .path("/login")
                            .toUriString();
                    boolean userExists = userRepo.existsByStaffId(user.getStaffId()); // Replace with appropriate check (email, etc.)

                    if(!userExists) {
                        String body = "<p>Dear, " + user.getName() + "</p>" +
                                "<p>Your account has been created.</p>" +
                                "<p>Login with your staff ID: <strong>" + user.getStaffId() + "</strong> and the default password: <strong>" + defaultPassword + "</strong>.</p>" +
                                "<p>Please change your password after logging in.</p>"+
                                "<p>You can log in here: <a href='" + loginUrl + "'>Login to CSMIS</a></p>";
                        emailService.sendEmail(user.getEmail(), subject, body);
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        userRepo.saveAll(users);
        return users;
    }

    @Override
    public ResponseDTO updateUserById(UserDTO userDTO, int id) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Staff not existed with this id.");
        }

        User tempUser = optionalUser.get();
        ResponseDTO res = new ResponseDTO();

        // Update basic fields
        tempUser.setName(userDTO.getName());
        tempUser.setStaffId(userDTO.getStaffId());
        tempUser.setDoorLogNo(Integer.valueOf(userDTO.getDoorLogNo()));
        tempUser.setEmail(userDTO.getEmail());
        tempUser.setIsActive("Active".equalsIgnoreCase(userDTO.getStatus()));
        tempUser.setIsVegan(userDTO.getIsVegan());

        if (!Objects.equals(tempUser.getRole(), userDTO.getRole())) {
            tempUser.setRole(userDTO.getRole());
        }

        // Find or create Division
        Division tempDivision = divisionRepo.findDivisionByName(userDTO.getDivision());
        if (tempDivision == null) {
            tempDivision = new Division();
            tempDivision.setName(userDTO.getDivision());
            divisionRepo.save(tempDivision);
        }
        tempUser.setDivision(tempDivision);

        // Find or create Department within the Division
        Department tempDepart = departmentRepo.findByNameAndDivision(userDTO.getDepartment(), tempDivision);
        if (tempDepart == null) {
            tempDepart = new Department();
            tempDepart.setName(userDTO.getDepartment());
            tempDepart.setDivision(tempDivision);
            departmentRepo.save(tempDepart);
        }
        tempUser.setDepartment(tempDepart);

        // Find or create Team within the Department
        Team tempTeam = teamRepo.findByNameAndDepartment(userDTO.getTeam(), tempDepart);
        if (tempTeam == null) {
            tempTeam = new Team();
            tempTeam.setName(userDTO.getTeam());
            tempTeam.setDepartment(tempDepart);
            teamRepo.save(tempTeam);
        }
        tempUser.setTeam(tempTeam);

        // Save the updated user
        User userSave = userRepo.save(tempUser);
        if (userSave != null) {
            res.setStatus("200");
            res.setMessage("Staff data updated successfully.");
        } else {
            res.setStatus("403");
            res.setMessage("Staff data update failed.");
        }

        return res;
    }

    public void updateDietaryPreference(DietaryPreferenceDTO preferenceDTO) {
        User user = userRepo.findById(preferenceDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getIsVegan() != preferenceDTO.getIsVegan()) {
            user.setIsVegan(preferenceDTO.getIsVegan());
        }

        List<Meat> meats = preferenceDTO.getMeatIds().stream()
                .map(meatId -> meatRepo.findById(meatId)
                        .orElseThrow(() -> new ResourceNotFoundException("Meat not found")))
                .collect(Collectors.toList());

        user.setMeats(meats);
        userRepo.save(user);
    }

    @Override
    public DietaryPreferenceDTO getDietaryPreferences(Integer userId) {
        Optional<User> user = userRepo.findById(userId);

        if (user.isPresent()) {
            User userEntity = user.get();

            DietaryPreferenceDTO dto = new DietaryPreferenceDTO();
            dto.setUserId(userEntity.getId());
            dto.setIsVegan(userEntity.getIsVegan());

            List<Integer> meatIds = userEntity.getMeats()
                    .stream()
                    .map(Meat::getId)
                    .collect(Collectors.toList());
            dto.setMeatIds(meatIds);

            return dto;
        }

        return null;
    }

    @Override
    public List<User> getAllAdmins() {
        return userRepo.findAllAdmins();
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
    public ResponseDTO toggleMail(int id, boolean mailOn) {
        Optional<User> optionalUser = userRepo.findById(id);
        ResponseDTO res = new ResponseDTO();
        if(optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        } else {
            User tempUser = optionalUser.get();
            tempUser.setReceivedMail(mailOn);
            User user = userRepo.save(tempUser);
            if(user == null) {
                res.setStatus("401");
                res.setMessage("Something went wrong");
            } else {
                res.setStatus("200");
                res.setMessage("Mail notifiication updated");
            }
        }
        return res;
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

        // Check if staff with the given ID already exists
        User staffExist = userRepo.findByStaffId(userDTO.getStaffId());
        if (staffExist != null) {
            res.setStatus("409");
            res.setMessage("Staff already existed with this id.");
            return res;
        }

        User tempUser = mapper.map(userDTO, User.class);

        // Find or create Division
        Division tempDivision = divisionRepo.findDivisionByName(userDTO.getDivision());
        if (tempDivision == null) {
            tempDivision = new Division();
            tempDivision.setName(userDTO.getDivision());
            divisionRepo.save(tempDivision);
        }
        // Set the found or new Division to the User (assuming User has a division field)
        tempUser.setDivision(tempDivision);

        // Find or create Department
        Department tempDepart = departmentRepo.findByNameAndDivision(userDTO.getDepartment(), tempDivision);
        if (tempDepart == null) {
            tempDepart = new Department();
            tempDepart.setName(userDTO.getDepartment());
            tempDepart.setDivision(tempDivision);
            departmentRepo.save(tempDepart);
        }
        // Set the found or new Department to the User (assuming User has a department field)
        tempUser.setDepartment(tempDepart);

        // Find or create Team
        Team tempTeam = teamRepo.findByNameAndDepartment(userDTO.getTeam(), tempDepart);
        if (tempTeam == null) {
            tempTeam = new Team();
            tempTeam.setName(userDTO.getTeam());
            tempTeam.setDepartment(tempDepart);
            teamRepo.save(tempTeam);
        }
        tempUser.setTeam(tempTeam);  // Set the found or new Team to the User

        // Set user status and default password
        tempUser.setIsActive("Active".equalsIgnoreCase(userDTO.getStatus()));
        tempUser.setIsVegan(false);
        String defaultPassword = getDefaultPassword();
        tempUser.setPassword(passwordEncoder.encode(defaultPassword));

        // Save the user
        User userSave = userRepo.save(tempUser);
        if (userSave != null) {
            res.setStatus("200");
            res.setMessage("Staff data inserted successfully.");
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
