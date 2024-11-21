package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.WeeklyCostsDTO;
import com.team2.csmis_api.dto.WeeklyPaymentDTO;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.service.SettingService;
import com.team2.csmis_api.service.UserHasLunchServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/admin/api")
public class WeeklyPaymentController {

    @Autowired
    private UserHasLunchServices userHasLunchServices;
    @Autowired
    private SettingService settingService;
    @GetMapping("/total-cost-and-date-count")
    public ResponseEntity<Map<String, Object>> getTotalCostAndDateCountForPreviousWeek(
            @RequestParam(required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateTotalCostAndDateCountForPreviousWeek(departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/total-cost-and-month-count/{month}/{year}")
    public ResponseEntity<Map<String, Object>> getTotalCostAndDateCountForMonth(
            @PathVariable("month") int month,
            @PathVariable("year") int year,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            // Call the service with the departmentId if it's provided, or null if not
            Map<String, Object> result = userHasLunchServices.calculateTotalCostAndDateCountForMonth(month, year, departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/total-cost-and-year-count/{year}")
    public ResponseEntity<Map<String, Object>> getTotalCostAndDateCountForYear(
            @PathVariable("year") int year,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateTotalCostAndDateCountForYear(year, departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/total-company-cost")
    public ResponseEntity<Map<String, Object>> getCompanyCostForPreviousWeek(
            @RequestParam Integer adminId, @RequestParam(required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateCompanyCostForPreviousWeek(adminId, departmentId);
            return ResponseEntity.ok(result);
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/total-company-cost-and-month-count/{month}/{year}")
    public ResponseEntity<Map<String, Object>> getTotalCompanyCostAndDateCountForMonth(
            @PathVariable("month") int month,
            @PathVariable("year") int year,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateTotalCompanyCostAndDateCountForMonth(month, year, departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/total-company-cost-and-year-count/{year}")
    public ResponseEntity<Map<String, Object>> getTotalCompanyCostAndDateCountForYear(
            @PathVariable("year") int year,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateTotalCompanyCostAndDateCountForYear(year, departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/all-total-cost-and-date-count")
    public ResponseEntity<Map<String, Object>> getAllTotalCostAndDateCountForPreviousWeek(
            @RequestParam(required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateAllTotalCostAndDateCountForPreviousWeek(departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/all-total-cost-and-year-count/{year}")
    public ResponseEntity<Map<String, Object>> getAllTotalCostAndDateCountForYear(
            @PathVariable("year") int year,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateAllTotalCostForYear(year, departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/all-total-cost-and-date-count/{month}/{year}")
    public ResponseEntity<Map<String, Object>> getAllTotalCostAndDateCountForMonth(
            @PathVariable("month") int month,
            @PathVariable("year") int year,
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {
        try {
            Map<String, Object> result = userHasLunchServices.calculateAllTotalCostAndDateCountForMonth(month, year, departmentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
