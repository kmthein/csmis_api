package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.LunchSummaryDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.RestaurantRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.UserRepository;
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
import java.io.InputStream;
import java.time.LocalDate;
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
    private UserHasLunchRepository userHasLunchRepo;

    @Autowired
    private UserService userService;

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

    public LunchSummaryDTO getMonthlyLunchSummary() {
        return userHasLunchRepo.getLunchSummaryBetweenTwo(LocalDate.now().withDayOfMonth(1).toString(), LocalDate.now().toString());
    }

    public LunchSummaryDTO getSummaryBetween(String startDate, String endDate) {
        return userHasLunchRepo.getLunchSummaryBetweenTwo(startDate, endDate);
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
}
