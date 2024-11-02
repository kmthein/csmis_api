package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.service.JasperReportService;
import com.team2.csmis_api.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

