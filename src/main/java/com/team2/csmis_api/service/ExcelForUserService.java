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
import org.springframework.stereotype.Service;
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
public class ExcelForUserService {
    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DivisionRepository divisionRepo;

    @Autowired
    private TeamRepository teamRepo;

    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<User> getUsersDataFromExcel(InputStream inputStream) {
        List<User> users = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Employee_Data");

            int rowIndex = 0;

            for (Row row : sheet) {
                if (rowIndex < 3) {
                    rowIndex++;
                    continue;
                }

                User user = null; // Create a new Holiday object
                user = userRepo.findByStaffId(row.getCell(2).getStringCellValue());
                System.out.println(user);
                if(user == null) {
                    user = new User();
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                Division division = null;
                Department department = null;
                Team team = null;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 1:
                            division = divisionRepo.findDivisionByName(cell.getStringCellValue());
                            if(division != null) {
                                if(!division.getName().equals(cell.getStringCellValue())) {
                                    division.setName(cell.getStringCellValue());
                                }
                            } else {
                                division = new Division();
                                division.setName(cell.getStringCellValue());
                            }
                            divisionRepo.save(division);
                            user.setDivision(division);
//                            user.setName(cell.getStringCellValue());
                            break;
                        case 2:
                            user.setStaffId(cell.getStringCellValue());
                            break;
                        case 3:
                            user.setName(cell.getStringCellValue());
                            break;
                        case 4:
                            user.setDoorLogNo((int) cell.getNumericCellValue());
                            break;
                        case 5:
                            department = departmentRepo.findDepartmentByName(cell.getStringCellValue());
                            if(department != null) {
                                if(!department.getName().equals(cell.getStringCellValue())) {
                                    department.setName(cell.getStringCellValue());
                                }
                            } else {
                                department = new Department();
                                department.setName(cell.getStringCellValue());
                            }
                            department.setDivision(division);
                            departmentRepo.save(department);
                            user.setDepartment(department);
                            break;
                        case 6:
                            team = teamRepo.findTeamByName(cell.getStringCellValue());
                            if(team != null) {
                                if(!team.getName().equals(cell.getStringCellValue())) {
                                    team.setName(cell.getStringCellValue());
                                }
                            } else {
                                team = new Team();
                                team.setName(cell.getStringCellValue());
                            }
                            team.setDepartment(department);
                            teamRepo.save(team);
                            user.setTeam(team);
                            break;
                        case 7:
                            if(cell.getStringCellValue().equals("Active")) {
                                user.setIsActive(true);
                            } else {
                                user.setIsActive(false);
                            }
                            break;
                        case 8:
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

        return users;
    }
}
