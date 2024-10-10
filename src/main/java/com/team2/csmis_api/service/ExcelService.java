package com.team2.csmis_api.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class ExcelService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static <T> List<T> getDataFromExcel(InputStream inputStream, String sheetName, Function<Row, T> rowMapper) {
        List<T> dataList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            System.out.println(sheet.getSheetName());

            int rowIndex = 4;
            for (Row row : sheet) {
                if (rowIndex == 4) {
                    rowIndex++; // Skip header row
                    continue;
                }
                T entity = rowMapper.apply(row); // Use rowMapper to convert row to entity
                if (entity != null) {
                    dataList.add(entity);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dataList;
    }
}
