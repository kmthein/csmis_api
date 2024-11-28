package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.*;
import com.team2.csmis_api.entity.OrderRow;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.service.UserHasLunchServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/lunch")
@CrossOrigin
public class UserHasLunchController {

    @Autowired
    private UserHasLunchServices userHasLunchService;

    @GetMapping("/lunch-count-next-week")
    public ResponseEntity<List<DateCountDTO>> getLunchCountsForNextWeek() {
        List<DateCountDTO> counts = userHasLunchService.getNextWeekLunchCounts();
        return ResponseEntity.ok(counts);
    }

    @PostMapping("")
    public ResponseEntity<?> registerForLunch(@RequestBody LunchRegistrationDTO registrationDto) {
        try {
            userHasLunchService.registerUserForLunch((registrationDto.getUserId()), registrationDto.getSelectedDates());
            return ResponseEntity.ok("Lunch registration successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering for lunch: " + e.getMessage());
        }
    }



@GetMapping("/selected-dates/{userId}")
public ResponseEntity<List<Date>> getSelectedDates(@PathVariable int userId) {
    List<Date> selectedLunches = userHasLunchService.getDtByUserId(userId);

    if (selectedLunches.isEmpty()) {
        return ResponseEntity.ok(Collections.emptyList());
    } else {
        return ResponseEntity.ok(selectedLunches);
    }
}

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateLunchRegistration(@PathVariable int userId, @RequestBody LunchRegistrationDTO registrationDto) {
        try {
            YearMonth currentMonth = YearMonth.now();

            List<Date> currentMonthDates = userHasLunchService.getDtByUserId(userId).stream()
                    .filter(date -> {
                        LocalDate registrationDate = date.toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();
                        YearMonth registrationMonth = YearMonth.from(registrationDate);
                        return registrationMonth.equals(currentMonth);
                    })
                    .collect(Collectors.toList());

            if (!currentMonthDates.isEmpty()) {
                userHasLunchService.deleteLunchRegistrations(userId);
            }

            userHasLunchService.registerUserForLunch(userId, registrationDto.getSelectedDates());

            return ResponseEntity.ok(Collections.singletonMap("message", "Lunch registration updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error updating lunch registration: " + e.getMessage()));
        }
    }

    @PutMapping("/update-next-month/{userId}")
    public ResponseEntity<?> updateLunchForNextMonth(@PathVariable Integer userId, @RequestBody LunchRegistrationDTO registrationDto) {
        try {
            userHasLunchService.updateLunchForNextMonth(userId, (List<Date>) registrationDto);
            return ResponseEntity.ok("Lunch registration for next month updated successfully for user ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating next month's lunch registration: " + e.getMessage());
        }
    }
    @GetMapping("/get-lunch-details/{userId}")
    public ResponseEntity<LunchDetailsDTO> getLunchDetails(@PathVariable Integer userId) {
        LunchDetailsDTO lunchDetails = userHasLunchService.getLunchDetails(userId);
        return ResponseEntity.ok(lunchDetails);
    }
    @GetMapping("/cost-per-day/user/{userId}")
    public ResponseEntity<Map<String, Object>> getLunchCostPerDayByUserId(@PathVariable Integer userId) {
        Map<String, Object> costDetails = userHasLunchService.calculateLunchCostPerDayByUserId(userId);
        return ResponseEntity.ok(costDetails);
    }
    @GetMapping("/cost")
    public Double getTotalCost(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return userHasLunchService.getCost(date);
    }

}
