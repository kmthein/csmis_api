package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.LunchRegistrationDTO;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.service.UserHasLunchServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/lunch")
@CrossOrigin(origins = "http://localhost:4200")
public class UserHasLunchController {

    @Autowired
    private UserHasLunchServices userHasLunchService;

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



}
