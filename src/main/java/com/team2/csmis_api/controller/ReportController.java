package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.UserActionDTO;
import com.team2.csmis_api.dto.LunchSummaryDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.service.JasperReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private JasperReportService reportService;

    @GetMapping("/restaurants")
    public void generateRestaurantReport(HttpServletResponse response) throws Exception {
        byte[] pdfReport = reportService.generateRestaurantReport();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=restaurant_report.pdf");
        response.getOutputStream().write(pdfReport);
        response.getOutputStream().flush();
    }

//    @GetMapping("/restaurant/view")
//    public ResponseEntity<byte[]> viewRestaurantReport() {
//        try {
//            byte[] report = reportService.generateRestaurantReport();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=restaurant_report.pdf")
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(report);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }

    @GetMapping("mail-on")
    public List<UserDTO> getMailOnUsers() {
        return reportService.getMailNotiOnUsers();
    }

    @PutMapping("lunch-summary")
    public LunchSummaryDTO getSummaryByInterval(@RequestParam(value = "date") String targetDate) throws Exception {
        return reportService.getDailyLunchSummary(LocalDate.parse(targetDate));
    }

    @PutMapping("monthly-summary")
    public LunchSummaryDTO getSummaryMonthly(@RequestParam(value = "month") String month, @RequestParam(value = "year") String year) {
        return reportService.getMonthlyLunchSummary(month, year);
    }

    @PutMapping("yearly-summary")
    public LunchSummaryDTO getSummaryMonthly(@RequestParam(value = "year") String year) {
        return reportService.getYearlyLunchSummary(year);
    }

    @PutMapping("summary-between")
    public LunchSummaryDTO getSummaryBetween(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return reportService.getSummaryBetween(startDate, endDate);
    }

    @GetMapping("lunch-summary/generate")
    public void generateReport(@RequestParam("date") String date, HttpServletResponse response) throws Exception {
        // Parse the dynamic date (e.g., from query parameters or request body)
        Date dynamicDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);

        // Generate the report
        JasperPrint jasperPrint = reportService.generateLunchSummary(dynamicDate);

        // Export the report to a PDF file
        response.setContentType("application/pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam String templatePath,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "report") String fileName,
            @RequestParam Map<String, ?> allParams) {
        try {
            Map<String, Object> parameters = new HashMap<>();  // Add parameters as needed
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Define date format

            for (String key : allParams.keySet()) {
                Object value = allParams.get(key);

                // Check if the value is a date string and convert it
                if (value instanceof String) {
                    String stringValue = (String) value;
                    try {
                        // Attempt to parse as date
                        Date dateValue = dateFormat.parse(stringValue);
                        parameters.put(key, dateValue);  // Store Date object
                    } catch (ParseException e) {
                        // Not a date, store the original string
                        parameters.put(key, stringValue);
                    }
                } else {
                    parameters.put(key, value);  // Store non-string values as-is
                }
            }
            byte[] reportData = reportService.generateReport(templatePath, fileType, parameters);

            String headerValue = (fileType.equalsIgnoreCase("pdf") ? "inline" : "attachment") + "; filename=" + fileName + "." + fileType;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .contentType(fileType.equalsIgnoreCase("pdf") ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(reportData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<byte[]> buildResponse(byte[] reportData, String fileType, String fileName) {
        String headerValue = (fileType.equalsIgnoreCase("pdf") ? "inline" : "attachment") + "; filename=" + fileName + "." + fileType;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(fileType.equalsIgnoreCase("pdf") ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportData);
    }
    // Daily report generation
    @GetMapping("/generate-daily")
    public ResponseEntity<byte[]> generateRADailyReport(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "dailyReport") String fileName) {
        try {
            byte[] reportData = reportService.generateDailyReport(date, fileType);
            return buildResponse(reportData, fileType, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Weekly report generation
    @GetMapping("/generate-weekly")
    public ResponseEntity<byte[]> generateRAWeeklyReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "weeklyReport") String fileName) {
        try {
            byte[] reportData = reportService.generateWeeklyReport(startDate, endDate, fileType);
            return buildResponse(reportData, fileType, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Monthly report generation
    @GetMapping("/generate-monthly")
    public ResponseEntity<byte[]> generateRAMonthlyReport(
            @RequestParam("month") String monthName,
            @RequestParam("year") int year,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "monthlyReport") String fileName) {
        try {
            Month monthEnum = Month.valueOf(monthName.toUpperCase());
            int month = monthEnum.getValue();

            byte[] reportData = reportService.generateMonthlyReport(month, year, fileType);
            return buildResponse(reportData, fileType, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/generate-yearly")
    public ResponseEntity<byte[]> generateRAYearlyReport(
            @RequestParam("year") int year,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "yearlyReport") String fileName) {
        try {
            byte[] reportData = reportService.generateYearlyReport(year, fileType);
            return buildResponse(reportData, fileType, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/registered-ate-daily")
    public List<UserActionDTO> searchRAByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getRegisteredAteByDate(date);
    }

    @GetMapping("/registered-ate-weekly")
    public ResponseEntity<List<UserActionDTO>> searchRAByWeek(
                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            List<UserActionDTO> reportData = reportService.getRegisteredAteByWeek(startDate, endDate);
            return ResponseEntity.ok(reportData);
        }

    @GetMapping("/registered-ate-monthly")
    public ResponseEntity<List<UserActionDTO>> searchRAByMonth(@RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
            List<UserActionDTO> reportData = reportService.getRegisteredAteByMonth(yearMonth);
            return ResponseEntity.ok(reportData);
        }

    @GetMapping("/registered-ate-yearly")
    public List<UserActionDTO> searchRAByYear(@RequestParam int year) {
        return reportService.getRegisteredAteByYear(year);
    }

}

