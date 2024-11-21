package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.UserActionDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.service.JasperReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
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

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam String templatePath,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "report") String fileName) {
        try {
            Map<String, Object> parameters = new HashMap<>();  // Add parameters as needed

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

    @GetMapping("/generate-daily")
    public ResponseEntity<byte[]> generateRADailyReport(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "dailyReport") String fileName,
            @RequestParam String templatePath) {
        try {
            byte[] reportData = reportService.generateDailyReport(templatePath, date, fileType);
            return buildResponse(reportData, fileType, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/generate-weekly")
    public ResponseEntity<byte[]> generateRAWeeklyReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "weeklyReport") String fileName,
            @RequestParam String templatePath) {
        try {
            byte[] reportData = reportService.generateWeeklyReport(templatePath, startDate, endDate, fileType);
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
            @RequestParam(defaultValue = "monthlyReport") String fileName,
            @RequestParam String templatePath) {
        try {
            Month monthEnum = Month.valueOf(monthName.toUpperCase());
            int month = monthEnum.getValue();

            byte[] reportData = reportService.generateMonthlyReport(templatePath,month,year,fileType);
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
            @RequestParam(defaultValue = "yearlyReport") String fileName,
            @RequestParam String templatePath) {
        try {
            byte[] reportData = reportService.generateYearlyReport(templatePath,year, fileType);
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
    public ResponseEntity<List<UserActionDTO>> getRegisteredAteByMonth(
            @RequestParam("month") String monthName,
            @RequestParam("year") int year) {
        try {
            Month monthEnum = Month.valueOf(monthName.toUpperCase());
            int month = monthEnum.getValue();

            List<UserActionDTO> reportData = reportService.getRegisteredAteByMonth(month, year);

            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }




    @GetMapping("/registered-ate-yearly")
    public List<UserActionDTO> searchRAByYear(@RequestParam int year) {
        return reportService.getRegisteredAteByYear(year);
    }

    @GetMapping("/unregistered-ate-daily")
    public List<UserActionDTO> searchURAByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getUnRegisteredAteByDate(date);
    }

    @GetMapping("/unregistered-ate-weekly")
    public ResponseEntity<List<UserActionDTO>> searchURAByWeek(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<UserActionDTO> reportData = reportService.getUnRegisteredAteByWeek(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/unregistered-ate-monthly")
    public ResponseEntity<List<UserActionDTO>> searchURAByMonth(
            @RequestParam("month") String monthName,
            @RequestParam("year") int year) {
        try {
            Month monthEnum = Month.valueOf(monthName.toUpperCase());
            int month = monthEnum.getValue();

            List<UserActionDTO> reportData = reportService.getUnRegisteredAteByMonth(month, year);

            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/unregistered-ate-yearly")
    public List<UserActionDTO> searchURAByYear(@RequestParam int year) {
        return reportService.getUnRegisteredAteByYear(year);
    }

    @GetMapping("/registered-not-eat-daily")
    public List<UserActionDTO> searchRNEByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getRegisteredNotEatByDate(date);
    }

    @GetMapping("/registered-not-eat-weekly")
    public ResponseEntity<List<UserActionDTO>> searchRNEByWeek(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<UserActionDTO> reportData = reportService.getRegisteredNotEatByWeek(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/registered-not-eat-monthly")
    public ResponseEntity<List<UserActionDTO>> searchRNEByMonth(
            @RequestParam("month") String monthName,
            @RequestParam("year") int year) {
        try {
            Month monthEnum = Month.valueOf(monthName.toUpperCase());
            int month = monthEnum.getValue();

            List<UserActionDTO> reportData = reportService.getRegisteredNotEatByMonth(month, year);

            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/registered-not-eat-yearly")
    public List<UserActionDTO> searchRNEByYear(@RequestParam int year) {
        return reportService.getRegisteredNotEatByYear(year);
    }

}

