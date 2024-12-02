package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.*;
import com.team2.csmis_api.entity.VoucherRow;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.VoucherRowRepository;
import com.team2.csmis_api.service.JasperReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private VoucherRowRepository voucherRowRepository;

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

    @PutMapping("lunch-cost")
    public List<MonthlyLunchCostDTO> getLunchCostsForMonth(@RequestParam(value = "date", required = false) String date,
                                                           @RequestParam(value = "month", required = false) String month,
                                                           @RequestParam(value = "year", required = false) String year,
                                                           @RequestParam(value = "start", required = false) String start,
                                                           @RequestParam(value = "end", required = false) String end) {
        return reportService.getLunchCostsByDepartment(date, month, year, start, end);
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam String templatePath,
            @RequestParam String fileType,
            @RequestParam(defaultValue = "report") String fileName,
            @RequestParam Map<String, String> allParams) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (String key : allParams.keySet()) {
                String value = allParams.get(key);
                try {
                    // Try parsing as an integer
                    int intValue = Integer.parseInt(value);
                    parameters.put(key, intValue);
                } catch (NumberFormatException intEx) {
                    try {
                        // Try parsing as a date
                        Date dateValue = dateFormat.parse(value);
                        parameters.put(key, dateValue);
                    } catch (ParseException dateEx) {
                        // Fallback: store the original string
                        parameters.put(key, value);
                    }
                }
            }

            byte[] reportData = reportService.generateReport(templatePath, fileType, parameters);

            String headerValue = (fileType.equalsIgnoreCase("pdf") ? "inline" : "attachment")
                    + "; filename=" + fileName + "." + fileType;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .contentType(fileType.equalsIgnoreCase("pdf") ? MediaType.APPLICATION_PDF
                            : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
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

    @PutMapping("/registered-ate-daily")
    public List<UserActionDTO> searchRAByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getRegisteredAteByDate(date);
    }

    @PutMapping("/registered-ate-weekly")
    public ResponseEntity<List<UserActionDTO>> searchRAByWeek(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<UserActionDTO> reportData = reportService.getRegisteredAteByWeek(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @PutMapping("/registered-ate-monthly")
    public ResponseEntity<List<UserActionDTO>> getRegisteredAteByMonth(
            @RequestParam("month") String month,
            @RequestParam("year") String year) {
        try {
            List<UserActionDTO> reportData = reportService.getRegisteredAteByMonth(Integer.parseInt(month), Integer.parseInt(year));

            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }


    @PutMapping("/registered-ate-yearly")
    public List<UserActionDTO> searchRAByYear(@RequestParam int year) {
        return reportService.getRegisteredAteByYear(year);
    }

    @PutMapping("/unregistered-ate-daily")
    public List<UserActionDTO> searchURAByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getUnRegisteredAteByDate(date);
    }

    @PutMapping("/unregistered-ate-weekly")
    public ResponseEntity<List<UserActionDTO>> searchURAByWeek(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<UserActionDTO> reportData = reportService.getUnRegisteredAteByWeek(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @PutMapping("/unregistered-ate-monthly")
    public ResponseEntity<List<UserActionDTO>> searchURAByMonth(
            @RequestParam("month") String month,
            @RequestParam("year") String year) {
        try {
            List<UserActionDTO> reportData = reportService.getUnRegisteredAteByMonth(Integer.parseInt(month), Integer.parseInt(year));

            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/unregistered-ate-yearly")
    public List<UserActionDTO> searchURAByYear(@RequestParam int year) {
        return reportService.getUnRegisteredAteByYear(year);
    }

    @PutMapping("/registered-not-eat-daily")
    public List<UserActionDTO> searchRNEByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getRegisteredNotEatByDate(date);
    }

    @PutMapping("/registered-not-eat-weekly")
    public ResponseEntity<List<UserActionDTO>> searchRNEByWeek(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<UserActionDTO> reportData = reportService.getRegisteredNotEatByWeek(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @PutMapping("/registered-not-eat-monthly")
    public ResponseEntity<List<UserActionDTO>> searchRNEByMonth(
            @RequestParam("month") String month,
            @RequestParam("year") String year) {
        try {
            List<UserActionDTO> reportData = reportService.getRegisteredNotEatByMonth(Integer.parseInt(month), Integer.parseInt(year));

            return ResponseEntity.ok(reportData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/registered-not-eat-yearly")
    public List<UserActionDTO> searchRNEByYear(@RequestParam int year) {
        return reportService.getRegisteredNotEatByYear(year);
    }

    @GetMapping("/users-avoid-meat")
    public ResponseEntity<List<AvoidMeatDTO>> getMeatLunchCountsForNextWeek() {
        List<AvoidMeatDTO> results = reportService.getUserAvoidMeatForNextWeek();
        return ResponseEntity.ok(results);
    }

    @PutMapping("/daily-company-cost")
    public ResponseEntity<List<CostDTO>> getDailyCompanyCosting(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CostDTO> results = reportService.getDailyCompanyCosting(date);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/weekly-company-cost")
    public ResponseEntity<List<CostDTO>> getWeeklyCompanyCosting(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CostDTO> results = reportService.getWeeklyCompanyCosting(startDate,endDate);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/monthly-company-cost")
    public ResponseEntity<List<CostDTO>> getMonthlyCompanyCosting(@RequestParam("month") String month,
                                                                  @RequestParam("year") String year) {
        List<CostDTO> results = reportService.getMonthlyCompanyCosting(Integer.parseInt(month), Integer.parseInt(year));
        return ResponseEntity.ok(results);
    }

    @PutMapping("/daily-employee-cost")
    public ResponseEntity<List<EmployeeCostDTO>> getDailyEmployeeOwnCost(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<EmployeeCostDTO> results = reportService.getDailyEmployeeOwnCost(date);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/weekly-employee-cost")
    public ResponseEntity<List<EmployeeCostDTO>> getWeeklyEmployeeOwnCost(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EmployeeCostDTO> results = reportService.getWeeklyEmployeeOwnCost(startDate,endDate);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/monthly-employee-cost")
    public ResponseEntity<List<EmployeeCostDTO>> getMonthlyEmployeeOwnCost(@RequestParam("month") String month,
                                                                  @RequestParam("year") String year) {
        List<EmployeeCostDTO> results = reportService.getMonthlyEmployeeOwnCost(Integer.parseInt(month), Integer.parseInt(year));
        return ResponseEntity.ok(results);
    }

    @GetMapping("/paid-voucher")
    public ResponseEntity<byte[]> getPaidVoucherReport() throws IOException {
        try {
            // Generate the report
            byte[] reportData = reportService.generatePaidVoucherReport();

            // Return the PDF as a response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=paid_voucher_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(500).body("Error generating report".getBytes());
        }
    }

    @PutMapping("/paid-voucher-list")
    public ResponseEntity<List<PaidVoucherDTO>> getPaidVoucher(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                          @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PaidVoucherDTO> results = reportService.getPaidVoucher(startDate,endDate);
        return ResponseEntity.ok(results);
    }
}

