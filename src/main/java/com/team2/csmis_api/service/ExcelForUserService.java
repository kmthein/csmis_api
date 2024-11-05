package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.repository.DepartmentRepository;
import com.team2.csmis_api.repository.DivisionRepository;
import com.team2.csmis_api.repository.TeamRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class ExcelForUserService {
    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DivisionRepository divisionRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "DAT110ct2";

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }

    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<User> getUsersDataFromExcel(InputStream inputStream) {
        List<User> users = new ArrayList<>();
        Set<String> processedStaffIds = new HashSet<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Employee_Data");

            int rowIndex = 0;

            for (Row row : sheet) {
                if (rowIndex < 3) {
                    rowIndex++;
                    continue;
                }

                String staffId = row.getCell(2).getStringCellValue();
                User user = userRepo.findByStaffId(row.getCell(2).getStringCellValue());

                if (user == null) {
                    user = new User();
                    String defaultPassword = getDefaultPassword();
                    user.setRole(Role.OPERATOR);
                    user.setPassword(passwordEncoder.encode(defaultPassword));
                }

                processedStaffIds.add(staffId);

                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                Division division = null;
                Department department = null;
                Team team = null;

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 1: // Division
                            String divisionName = cell.getStringCellValue();
                            division = divisionRepo.findDivisionByName(divisionName);
                            if (division == null) {
                                division = new Division();
                                division.setName(divisionName);
                                divisionRepo.save(division);
                            }
                            user.setDivision(division);
                            break;

                        case 2: // Staff ID
                            user.setStaffId(cell.getStringCellValue());
                            break;

                        case 3: // Name
                            user.setName(cell.getStringCellValue());
                            break;

                        case 4: // Door Log No
                            user.setDoorLogNo((int) cell.getNumericCellValue());
                            break;

                        case 5: // Department
                            String departmentName = cell.getStringCellValue();
                            department = departmentRepo.findByNameAndDivision(departmentName, division);
                            if (department == null) {
                                department = new Department();
                                department.setName(departmentName);
                                department.setDivision(division);
                                departmentRepo.save(department);
                            }
                            user.setDepartment(department);
                            break;

                        case 6: // Team
                            String teamName = cell.getStringCellValue();
                            team = teamRepo.findByNameAndDepartment(teamName, department);
                            if (team == null) {
                                team = new Team();
                                team.setName(teamName);
                                team.setDepartment(department);
                                teamRepo.save(team);
                            }
                            user.setTeam(team);
                            break;

                        case 7: // Is Active
                            user.setIsActive("Active".equals(cell.getStringCellValue()));
                            break;

                        case 8: // Email
                            user.setEmail(cell.getStringCellValue());
                            break;

                        default:
                            break;
                    }
                    cellIndex++;
                }
                users.add(user);
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        deactivateUsersNotInExcel(processedStaffIds);
        return users;
    }

    private void deactivateUsersNotInExcel(Set<String> processedStaffIds) {
        List<User> allUsers = userRepo.findAll();
        for (User user : allUsers) {
            if (!processedStaffIds.contains(user.getStaffId())) {
                user.setIsActive(false);
            }
        }
        userRepo.saveAll(allUsers);
    }
}
