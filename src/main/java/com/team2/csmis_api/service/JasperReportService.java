package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.RestaurantRepository;
import com.team2.csmis_api.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public byte[] generateReport(String reportName, List<?> data, Map<String, Object> parameters, String format) throws Exception {
        // Load the Jasper report from resources folder
        InputStream reportStream = new ClassPathResource("reports/" + reportName + ".jasper").getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

        // Fill the report with data
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (format.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        }
        // Add additional format handling here (e.g., XLSX)

        return outputStream.toByteArray();
    }
}
