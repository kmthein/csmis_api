package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.UserActionDTO;
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
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.*;

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


    public List<UserDTO> getMailNotiOnUsers() {
        List<User> tempUserList = userRepository.getMailNotiOnUsers();
        List<UserDTO> userDTOList = new ArrayList<>();
        for(User user: tempUserList) {
            UserDTO userDTO = userService.mapUserToDTO(user);
            userDTOList.add(userDTO);
        }
        return userDTOList;
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
}
