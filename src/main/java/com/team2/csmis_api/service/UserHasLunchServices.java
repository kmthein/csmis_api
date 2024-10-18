package com.team2.csmis_api.service;

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

}