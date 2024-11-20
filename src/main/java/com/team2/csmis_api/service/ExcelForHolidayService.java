package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.Holiday;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelForHolidayService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<Holiday> getHolidaysDataFromExcel(InputStream inputStream) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("Holidays");

            int rowIndex = 0;


            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }

                Holiday holiday = new Holiday(); // Create a new Holiday object
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0:
                            String dateString = cell.getStringCellValue().trim();
                            System.out.println("Reading date string: " + dateString);

                            dateString = dateString.replaceAll("(\\d+)(st|nd|rd|th)", "$1");

                            dateString = dateString.split(",")[0].trim();

                            try {

                                String[] parts = dateString.split(" ");
                                int day = Integer.parseInt(parts[0]);
                                Month month = Month.valueOf(parts[1].toUpperCase());


                                LocalDate parsedDate = LocalDate.of(LocalDate.now().getYear(), month.getValue(), day);
                                holiday.setDate(parsedDate);
                            } catch (DateTimeParseException | NumberFormatException e) {
                                System.err.println("Failed to parse date: " + dateString);
                            }
                            break;



                        case 1:
                            holiday.setName(cell.getStringCellValue());
                            break;

                        default:

                            break;
                    }
                    cellIndex++;
                }
                holidays.add(holiday);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return holidays;
    }

}
