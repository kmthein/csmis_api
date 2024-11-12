package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.repository.LocationRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class ExcelForDoorLogService {

    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private UserRepository userRepo;

    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<DoorAccessRecord> getDoorLogDataFromExcel(InputStream inputStream) {
        List<DoorAccessRecord> doorAccessRecords = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("DoorAccess_TransactionData");

            int rowIndex = 0;

            for (Row row : sheet) {
                if (rowIndex < 1) {
                    rowIndex++;
                    continue;
                }

                DoorAccessRecord doorAccessRecord = new DoorAccessRecord();
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                Location location = null;
                User doorLog = null;
                LocalDateTime dateTime = null;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0:
                            if (cell.getCellType() == CellType.STRING) {
                                doorAccessRecord.setDepartment(cell.getStringCellValue());
                            }
                            break;
                        case 2:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                doorLog = userRepo.findByDoorLogNo((int) cell.getNumericCellValue());
                                if (doorLog != null) {
                                    doorAccessRecord.setUser(doorLog);
                                    doorAccessRecord.setDoorLogNo((int) cell.getNumericCellValue());
                                }
                            }
                            break;
                        case 3:
                            if (cell.getCellType() == CellType.STRING) {
                                String dateStr = cell.getStringCellValue();
                                try {
                                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                                    dateTime = LocalDateTime.parse(dateStr, dateFormat);
                                } catch (DateTimeParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (cell.getCellType() == CellType.NUMERIC) {
                                dateTime = cell.getLocalDateTimeCellValue();
                            }
                            doorAccessRecord.setDate(dateTime);
                            break;
                        case 4:
                            if (cell.getCellType() == CellType.STRING) {
                                int locationId = Integer.parseInt(cell.getStringCellValue());
                                location = locationRepo.findById(locationId);
                                if (location != null) {
                                    doorAccessRecord.setLocation(location);
                                }
                            }else if (cell.getCellType() == CellType.NUMERIC) {
                                int locationId = (int) cell.getNumericCellValue();
                                location = locationRepo.findById(locationId);
                                if (location != null) {
                                    doorAccessRecord.setLocation(location);
                                }
                            }
                            break;
                    }
                    cellIndex++;
                }
                doorAccessRecords.add(doorAccessRecord);
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doorAccessRecords;
    }

}
