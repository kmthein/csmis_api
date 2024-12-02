package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.*;
import com.team2.csmis_api.entity.DoorAccessRecord;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JasperReportService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DoorLogRepository doorLogRepo;

    @Autowired
    private LunchRepository lunchRepo;

    @Autowired
    private UserHasLunchRepository userHasLunchRepo;

    @Value("classpath:/reports/lunch-summary-daily.jasper")  // Path to your precompiled .jasper report
    private Resource reportResource;

    public JasperPrint generateLunchSummary(Date targetDate) throws Exception {
        // Load the Jasper report
        InputStream reportStream = reportResource.getInputStream();

        // Parameters for the report
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportDate", targetDate);


        // Fill the report with data and parameters
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource.getConnection());

        return jasperPrint;
    }

    public List<UserDTO> getMailNotiOnUsers() {
        List<User> tempUserList = userRepository.getMailNotiOnUsers();
        List<UserDTO> userDTOList = new ArrayList<>();
        for(User user: tempUserList) {
            UserDTO userDTO = userService.mapUserToDTO(user);
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    public LunchSummaryDTO getDailyLunchSummary(LocalDate targetDate) {
        return userHasLunchRepo.getDailyData(targetDate.toString());
    }

    public LunchSummaryDTO getMonthlyLunchSummary(String month, String year) {
        return userHasLunchRepo.lunchSummaryByMonthYear(month, year);
    }

    public LunchSummaryDTO getYearlyLunchSummary(String year) {
        return userHasLunchRepo.lunchSummaryByYear(year);
    }

    public LunchSummaryDTO getSummaryBetween(String startDate, String endDate) {
        return userHasLunchRepo.getLunchSummaryBetweenTwo(startDate, endDate);
    }

    public List<MonthlyLunchCostDTO> getLunchCostsByDepartment(String date, String month, String year, String start, String end) {
        List<Object[]> results = new ArrayList<>();
        if(start != null && end != null) {
            results = userHasLunchRepo.getLunchCostBetweenTwoDate(start, end);
            return results.stream()
                    .map(row -> new MonthlyLunchCostDTO((String) row[0], (String) row[1], (String) row[2], (Double) row[3]))
                    .collect(Collectors.toList());
        } else if(month == null && date == null) {
            results = userHasLunchRepo.getLunchCostByYearly(year);
            return results.stream()
                    .map(row -> new MonthlyLunchCostDTO((String) row[0], (String) row[1], (Double) row[2]))
                    .collect(Collectors.toList());
        } else if(year != null && month != null && date == null) {
            results = userHasLunchRepo.getLunchCostByYearAndMonth(month, year);
            return results.stream()
                    .map(row -> new MonthlyLunchCostDTO((String) row[0], (String) row[1], (String) row[2], (Double) row[3]))
                    .collect(Collectors.toList());
        } else if(date != null) {
            results = userHasLunchRepo.getLunchCostByDay(date);
            return results.stream()
                    .map(row -> new MonthlyLunchCostDTO((String) row[0], (String) row[1], (Double) row[2]))
                    .collect(Collectors.toList());
        }else {
            results = userHasLunchRepo.getMonthlyLunchCostByDepartment(month);
            return results.stream()
                    .map(row -> new MonthlyLunchCostDTO((String) row[0], (String) row[1], (Double) row[2]))
                    .collect(Collectors.toList());
        }
    }

    public String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public byte[] generateReport(String reportTemplatePath, String fileType, Map<String, Object> parameters) throws Exception {
        // Load the Jasper report template from resources
        InputStream reportStream = new ClassPathResource("reports/" + reportTemplatePath + ".jrxml").getInputStream();

        // Compile the report
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Fill the report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

        // Export the report based on the specified file type
        if ("pdf".equalsIgnoreCase(fileType)) {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } else if ("excel".equalsIgnoreCase(fileType)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            exporter.exportReport();
            return byteArrayOutputStream.toByteArray();
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public List<Restaurant> fetchData(int month, int year) {
        return restaurantRepository.getAllRestaurants();
    }

    public byte[] generateDailyReport(String reportTemplatePath, LocalDate date, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timeRangeType", "daily");
        parameters.put("date", java.sql.Date.valueOf(date));
        return generateReport(reportTemplatePath, fileType, parameters);
    }

    public byte[] generateWeeklyReport(String reportTemplatePath, LocalDate startDate, LocalDate endDate, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timeRangeType", "weekly");
        parameters.put("startDate", java.sql.Date.valueOf(startDate));
        parameters.put("endDate", java.sql.Date.valueOf(endDate));
        return generateReport(reportTemplatePath, fileType, parameters);
    }

    public byte[] generateMonthlyReport(String reportTemplatePath, int month, int year, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timeRangeType", "monthly");
        parameters.put("month", month);
        parameters.put("year", year);
        return generateReport(reportTemplatePath, fileType, parameters);
    }

    public byte[] generateYearlyReport(String reportTemplatePath, int year, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("timeRangeType", "yearly");
        parameters.put("year", year);
        return generateReport(reportTemplatePath, fileType, parameters);
    }


    public List<UserActionDTO> getRegisteredAteByDate(LocalDate date) {
        List<DoorAccessRecord> logs = doorLogRepo.findRegisteredAteByDate(date);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getRegisteredAteByWeek(LocalDate startDate, LocalDate endDate) {
        List<DoorAccessRecord> logs = doorLogRepo.findRegisteredAteByWeek(startDate, endDate);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getRegisteredAteByMonth(int month, int year) {
        List<DoorAccessRecord> logs = doorLogRepo.findRegisteredAteByMonth(month, year);

        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);

        return reportData;
    }

    public List<UserActionDTO> getRegisteredAteByYear(int year) {
        List<DoorAccessRecord> logs = doorLogRepo.findRegisteredAteByYear(year);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    private void processLogs(List<DoorAccessRecord> logs, List<UserActionDTO> reportData) {
        for (DoorAccessRecord record : logs) {
            UserActionDTO dto = new UserActionDTO();
            dto.setName(record.getUser().getName());
            dto.setDoorLogNo(record.getDoorLogNo());
            dto.setDate(record.getDate().toLocalDate());
            dto.setTeam(record.getUser().getTeam().getName());
            dto.setDepartment(record.getUser().getDepartment().getName());
            dto.setUserId(record.getUser().getId());
            dto.setId(record.getId());
            reportData.add(dto);
        }
    }

    public List<UserActionDTO> getUnRegisteredAteByDate(LocalDate date) {
        List<DoorAccessRecord> logs = doorLogRepo.findUnRegisteredAteByDate(date);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getUnRegisteredAteByWeek(LocalDate startDate, LocalDate endDate) {
        List<DoorAccessRecord> logs = doorLogRepo.findUnRegisteredAteByWeek(startDate, endDate);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getUnRegisteredAteByMonth(int month, int year) {
        List<DoorAccessRecord> logs = doorLogRepo.findUnRegisteredAteByMonth(month, year);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getUnRegisteredAteByYear(int year) {
        List<DoorAccessRecord> logs = doorLogRepo.findUnRegisteredAteByYear(year);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLogs(logs, reportData);
        return reportData;
    }

    private void processLog(List<UserHasLunch> logs, List<UserActionDTO> reportData) {
        for (UserHasLunch record : logs) {
            UserActionDTO dto = new UserActionDTO();
            dto.setName(record.getUser().getName());
            dto.setDoorLogNo(record.getUser().getDoorLogNo());
            dto.setDate(record.getDt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            dto.setUserId(record.getUser().getId());
            dto.setTeam(record.getUser().getTeam().getName());
            dto.setDepartment(record.getUser().getDepartment().getName());
            dto.setId(record.getId());
            reportData.add(dto);
        }
    }

    public List<UserActionDTO> getRegisteredNotEatByDate(LocalDate date) {
        List<UserHasLunch> logs = userHasLunchRepo.findRegisteredNotEatDaily(date);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLog(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getRegisteredNotEatByWeek(LocalDate startDate, LocalDate endDate) {
        List<UserHasLunch> logs = userHasLunchRepo.findRegisteredNotEatWeekly(startDate, endDate);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLog(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getRegisteredNotEatByMonth(int month, int year) {
        List<UserHasLunch> logs = userHasLunchRepo.findRegisteredNotEatMonthly(month, year);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLog(logs, reportData);
        return reportData;
    }

    public List<UserActionDTO> getRegisteredNotEatByYear(int year) {
        List<UserHasLunch> logs = userHasLunchRepo.findRegisteredNotEatYearly(year);
        List<UserActionDTO> reportData = new ArrayList<>();
        processLog(logs, reportData);
        return reportData;
    }

    public List<AvoidMeatDTO> getUserAvoidMeatForNextWeek() {

        List<Object[]> results = userHasLunchRepo.getUserAvoidMeatForNextWeek();

        return results.stream()
                .map(result -> new AvoidMeatDTO(
                        (String) result[0],
                        (String) result[1],
                        ((Number) result[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public List<CostDTO> getDailyCompanyCosting(LocalDate date) {
        List<Object[]> results = lunchRepo.getDailyCompanyCosting(date);
        return results.stream()
                .map(result -> new CostDTO(
                        (Date) result[0],
                        (Integer) result[1],
                        (Long) result[2],
                        (Double) result[3],
                        (Double) result[4],
                        (Double) result[5],
                        (Double) result[6]
                ))
                .collect(Collectors.toList());
    }

    public List<CostDTO> getWeeklyCompanyCosting(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = lunchRepo.getWeeklyCompanyCosting(startDate,endDate);
        return results.stream()
                .map(result -> new CostDTO(
                        (Date) result[0],
                        (Integer) result[1],
                        (Long) result[2],
                        (Double) result[3],
                        (Double) result[4],
                        (Double) result[5],
                        (Double) result[6]
                ))
                .collect(Collectors.toList());
    }

    public List<CostDTO> getMonthlyCompanyCosting(int month, int year) {
        List<Object[]> results = lunchRepo.getMonthlyCompanyCosting(month,year);
        return results.stream()
                .map(result -> new CostDTO(
                        (Date) result[0],
                        (Integer) result[1],
                        (Long) result[2],
                        (Double) result[3],
                        (Double) result[4],
                        (Double) result[5],
                        (Double) result[6]
                ))
                .collect(Collectors.toList());
    }

    public List<EmployeeCostDTO> getDailyEmployeeOwnCost(LocalDate date) {
        List<Object[]> results = lunchRepo.getDailyEmployeeOwnCost(date);
        return results.stream()
                .map(result -> new EmployeeCostDTO(
                        (String) result[0],
                        (String) result[1],
                        (Double) result[2],
                        (Double) result[3]
                ))
                .collect(Collectors.toList());
    }

    public List<EmployeeCostDTO> getWeeklyEmployeeOwnCost(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = lunchRepo.getWeeklyEmployeeOwnCost(startDate, endDate);
        return results.stream()
                .map(result -> new EmployeeCostDTO(
                        (String) result[0],
                        (String) result[1],
                        (Double) result[2],
                        (Double) result[3]
                ))
                .collect(Collectors.toList());
    }

    public List<EmployeeCostDTO> getMonthlyEmployeeOwnCost(int month, int year) {
        List<Object[]> results = lunchRepo.getMonthlyEmployeeOwnCost(month,year);
        return results.stream()
                .map(result -> new EmployeeCostDTO(
                        (String) result[0],
                        (String) result[1],
                        (Double) result[2],
                        (Double) result[3]
                ))
                .collect(Collectors.toList());
    }
    public byte[] generatePaidVoucherReport() {
        try {
            // Load the compiled Jasper report
            InputStream reportStream = getClass().getResourceAsStream("/reports/PaidVoucherListReport.jasper");

            // Ensure the report file is found
            if (reportStream == null) {
                throw new RuntimeException("Jasper report file not found");
            }

            // Set parameters if any
            Map<String, Object> parameters = new HashMap<>();
            // Example: parameters.put("ReportTitle", "Paid Voucher Report");

            // DataSource setup, assuming you're using a JDBC connection or other data source
            JRDataSource dataSource = new JREmptyDataSource();  // Replace with your actual data source

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);

            // Export the report to PDF
            byte[] pdfData = JasperExportManager.exportReportToPdf(jasperPrint);
            return pdfData;
        } catch (JRException e) {
            throw new RuntimeException("Error generating report", e);
        }
    }

//    public byte[] generatePaidVoucherReport() throws JRException, IOException {
//        // Load the compiled .jasper report
//        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new ClassPathResource("reports/PaidVoucherListReport.jasper").getInputStream());
//
//        // Create parameters for the report (empty or with dynamic values)
//        Map<String, Object> parameters = new HashMap<>();
//        // You can add parameters to the report, e.g., parameters.put("date", new Date());
//
//        // Fill the report with data (using a data source, for example)
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
//
//        // Export the report to a byte array (PDF format in this case)
//        return JasperExportManager.exportReportToPdf(jasperPrint);
//    }
}
