package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.MonthlyLunchCostDTO;
import com.team2.csmis_api.dto.UserActionDTO;
import com.team2.csmis_api.dto.LunchSummaryDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.DoorAccessRecord;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.repository.DoorLogRepository;
import com.team2.csmis_api.repository.RestaurantRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
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

    public byte[] generateRestaurantReport() throws Exception {
        // Load the Jasper report template from resources
        InputStream reportStream = new ClassPathResource("reports/restaurant2.jrxml").getInputStream();

        // Compile the report
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Set report parameters if needed (useful for dynamic data)
        Map<String, Object> parameters = new HashMap<>();

        // Fill the report with data from the database
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());

        // Export the report to a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public List<Restaurant> fetchData(int month, int year) {
        return restaurantRepository.getAllRestaurants();
    }

    public byte[] generateDailyReport(LocalDate date, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportDate", java.sql.Date.valueOf(date));
        return generateReport("dailyReport", fileType, parameters);
    }

    public byte[] generateWeeklyReport(LocalDate startDate, LocalDate endDate, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        Date reportStartDate = java.sql.Date.valueOf(startDate);
        Date reportEndDate = java.sql.Date.valueOf(endDate);
        parameters.put("startDate", reportStartDate);
        parameters.put("endDate", reportEndDate);
        return generateReport("weeklyReport", fileType, parameters);
    }

    public byte[] generateMonthlyReport(int month, int year, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("month", month);
        parameters.put("year", year);

        return generateReport("monthlyReport", fileType, parameters);
    }

    public byte[] generateYearlyReport(int year, String fileType) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("year", year);
        return generateReport("yearlyReport", fileType, parameters);
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

    public List<UserActionDTO> getRegisteredAteByMonth(YearMonth date) {
        List<DoorAccessRecord> logs = doorLogRepo.findRegisteredAteByMonth(date);
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
            dto.setUserId(record.getUser().getId());
            dto.setId(record.getId());
            reportData.add(dto);
        }
    }
}
