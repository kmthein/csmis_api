package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.LunchRegistrationDTO;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.stream.Collectors;


@Service
public class UserHasLunchServices {

    @Autowired
    private UserHasLunchRepository userHasLunchRepository;

    @Autowired
    private UserRepository userRepository;

    public void registerUserForLunch(Integer userId, List<Date> selectedDates) throws Exception {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new Exception("User not found with id: " + userId);
        }

        User user = userOptional.get();

        for (Date date : selectedDates) {
            UserHasLunch userHasLunch = new UserHasLunch();
            userHasLunch.setUser(user);
            userHasLunch.setDt(date);
            userHasLunch.setUserCost(calculateUserCost());
            userHasLunchRepository.save(userHasLunch);
        }
    }


    private double calculateUserCost() {
        return 10.0; // Example cost per lunch
    }


    public List<Date> getDtByUserId(int userId) {
        return userHasLunchRepository.findDtByUserId(userId);

    }



    public void deleteLunchRegistrations(int userId) {
        YearMonth currentMonth = YearMonth.now();
        List<UserHasLunch> userRegistrations = userHasLunchRepository.findByUserId(userId);
        for (UserHasLunch registration : userRegistrations) {
            LocalDate registrationDate = registration.getDt().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            YearMonth registrationMonth = YearMonth.from(registrationDate);
            if (registrationMonth.equals(currentMonth)) {
                userHasLunchRepository.delete(registration);
            }
        }
    }

    public void updateLunchForNextMonth(Integer userId, LunchRegistrationDTO registrationDto) {
        // Get the current date and next month
        YearMonth currentMonth = YearMonth.now();
        YearMonth nextMonth = currentMonth.plusMonths(1);

        // Fetch all lunch registrations for the user
        List<UserHasLunch> currentLunchRegistrations = userHasLunchRepository.findByUserId(userId);

        // Filter the registrations for the next month
        List<UserHasLunch> nextMonthRegistrations = currentLunchRegistrations.stream()
                .filter(registration -> {
                    LocalDate registrationDate = registration.getDt().toInstant()
                            .atZone(ZoneId.systemDefault())  // Convert Date to ZonedDateTime
                            .toLocalDate();  // Convert to LocalDate
                    YearMonth registrationMonth = YearMonth.from(registrationDate);
                    return registrationMonth.equals(nextMonth);  // Filter only next month's registrations
                })
                .collect(Collectors.toList());

        if (!nextMonthRegistrations.isEmpty()) {
            userHasLunchRepository.deleteAll(nextMonthRegistrations);
        }

        // Now, register the user's lunch for the next month
        List<UserHasLunch> newNextMonthLunches = generateLunchForNextMonth(userId, nextMonth);

        // Save the new lunch registrations for next month
        userHasLunchRepository.saveAll(newNextMonthLunches);
    }

    private List<UserHasLunch> generateLunchForNextMonth(Integer userId, YearMonth month) {
        LocalDate firstDayOfMonth = month.atDay(1);
        LocalDate lastDayOfMonth = month.atEndOfMonth();

        return firstDayOfMonth.datesUntil(lastDayOfMonth.plusDays(1))
                .filter(date -> date.getDayOfWeek().getValue() <= 5)
                .map(date -> {
                    UserHasLunch registration = new UserHasLunch();

                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

                    registration.setUser(user);
                    registration.setDt(java.sql.Date.valueOf(date));
                    registration.setUserCost(0);
                    return registration;
                })
                .collect(Collectors.toList());
    }

}