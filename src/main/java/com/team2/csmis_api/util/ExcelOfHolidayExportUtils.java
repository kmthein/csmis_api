package com.team2.csmis_api.util;

import com.team2.csmis_api.entity.Holiday;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelOfHolidayExportUtils {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Holiday> holidayList;

    public ExcelOfHolidayExportUtils(List<Holiday> holidayList){
        this.holidayList = holidayList;
        workbook = new XSSFWorkbook();
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if(value instanceof Integer){
            cell.setCellValue((Integer) value);
        }else if (value instanceof Double){
            cell.setCellValue((Double) value);
        }else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else if (value instanceof LocalDate) {
            LocalDate date = (LocalDate) value;
            String dayOfMonthSuffix = getDayOfMonthSuffix(date.getDayOfMonth());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM, EEEE"); // e.g., "January, Wednesday"
            String formattedDate = date.getDayOfMonth() + dayOfMonthSuffix + " " + date.format(formatter);
            cell.setCellValue(formattedDate);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";  // 11th, 12th, and 13th are exceptions
        }
        switch (day % 10) {
            case 1: return "st";  // 1st, 21st, 31st
            case 2: return "nd";  // 2nd, 22nd
            case 3: return "rd";  // 3rd, 23rd
            default: return "th"; // Everything else is "th"
        }
    }

    private CellStyle createCellStyle() {
        CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Set border color to green
        XSSFColor greenColor = new XSSFColor(new java.awt.Color(0, 128, 0), new DefaultIndexedColorMap());
        ((XSSFCellStyle) style).setTopBorderColor(greenColor);
        ((XSSFCellStyle) style).setBottomBorderColor(greenColor);
        ((XSSFCellStyle) style).setLeftBorderColor(greenColor);
        ((XSSFCellStyle) style).setRightBorderColor(greenColor);

        return style;
    }

    private void createHeaderRow(){
        sheet = workbook.createSheet("Holidays");
        Row row = sheet.createRow(0);
        CellStyle style = createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        //style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Holidays in 2024", style);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0, 1));
        font.setFontHeightInPoints((short) 15);


        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row,0,"Date",style);
        createCell(row,1,"Name",style);
    }

    private void writeHolidayData(){
        int rowCount=2;
        CellStyle style = createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont((font));

        for(Holiday holiday : holidayList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++,holiday.getDate(),style);
            createCell(row, columnCount++,holiday.getName(),style);

        }
    }

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createHeaderRow();
        writeHolidayData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
