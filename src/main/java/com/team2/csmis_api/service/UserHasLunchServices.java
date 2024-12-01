package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.*;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Settings;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.SettingRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;


@Service
public class UserHasLunchServices {

    @Autowired
    private UserHasLunchRepository userHasLunchRepository;
    @Autowired
    private LunchRepository lunchRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingRepository settingsRepository;

    public BigDecimal getUserWeeklyTotalCost(int userId) {
        return userHasLunchRepository.getWeeklyTotalUserCostByUserId(userId);
    }


    public BigDecimal getUserMonthlyTotalCost(int userId) {
        return userHasLunchRepository.getMonthlyTotalUserCostByUserId(userId);
    }

    public BigDecimal getUserYearlyTotalCost(int userId) {
        return userHasLunchRepository.getYearlyTotalUserCostByUserId(userId);
    }

    public List<DateCountDTO> getNextWeekLunchCounts() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate next Monday
        LocalDate nextMonday = today.with(DayOfWeek.MONDAY).isAfter(today) ?
                today.with(DayOfWeek.MONDAY) :
                today.plusWeeks(1).with(DayOfWeek.MONDAY);

        // Calculate next Friday
        LocalDate nextFriday = nextMonday.plusDays(4);

        // Format dates to String
        Date startDate = java.sql.Date.valueOf(nextMonday);
        Date endDate = java.sql.Date.valueOf(nextFriday);

        // Fetch data from repository
        return userHasLunchRepository.getLunchCounts(startDate, endDate);
    }

    public void registerUserForLunch(Integer userId, List<Date> selectedDates) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));
        Settings settings = settingsRepository.findTopByOrderByIdDesc();
        if (settings == null) {
            throw new Exception("Settings not configured");
        }

        double companyRate = settings.getCompanyRate();
        double lunchPrice = settings.getCurrentLunchPrice();
        double userSharePercentage = 100 - companyRate;
        double userCostPerDay = (lunchPrice * userSharePercentage) / 100;
        double companyCostPerDay = (lunchPrice * companyRate) / 100;

        double totalUserCost = 0;
        double totalCompanyCost = 0;
        for (Date date : selectedDates) {
            totalUserCost += userCostPerDay;
            totalCompanyCost += companyCostPerDay;

            UserHasLunch userHasLunch = new UserHasLunch();
            userHasLunch.setUser(user);
            userHasLunch.setDt(date);
            userHasLunch.setUserCost(userCostPerDay);
            userHasLunch.setCompany_cost(companyCostPerDay);
            double totalCost = userCostPerDay + companyCostPerDay;
            userHasLunch.setTotal_cost(totalCost);
            System.out.println("Look::  " + userHasLunch);
            userHasLunchRepository.save(userHasLunch);
        }




        userRepository.save(user);
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

    public void updateLunchForNextMonth(Integer userId, List<Date> selectedDates) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));
        Settings settings = settingsRepository.findTopByOrderByIdDesc();
        if (settings == null) {
            throw new Exception("Settings not configured");
        }

        double companyRate = settings.getCompanyRate();
        double lunchPrice = settings.getCurrentLunchPrice();
        double userSharePercentage = 100 - companyRate;
        double userCostPerDay = (lunchPrice * userSharePercentage) / 100;
        double companyCostPerDay = (lunchPrice * companyRate) / 100;

        double totalUserCost = 0;
        double totalCompanyCost = 0;
        for (Date date : selectedDates) {
            totalUserCost += userCostPerDay;
            totalCompanyCost += companyCostPerDay;

            UserHasLunch userHasLunch = new UserHasLunch();
            userHasLunch.setUser(user);
            userHasLunch.setDt(date);
            userHasLunch.setUserCost(userCostPerDay);
            userHasLunch.setCompany_cost(companyCostPerDay);
            double totalCost = userCostPerDay + companyCostPerDay;
            userHasLunch.setTotal_cost(totalCost);
            userHasLunchRepository.save(userHasLunch);
        }


        userRepository.save(user);
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

    public LunchDetailsDTO getLunchDetails(Integer userId) {
        int registeredDays = userHasLunchRepository.countRegisteredDaysForMonth(userId);
        Settings settings = settingsRepository.findLatestSettings();
        if (settings == null) {
            throw new RuntimeException("Settings not found!");
        }

        double lunchPrice = settings.getCurrentLunchPrice();
        double companyRate = settings.getCompanyRate();
        double userSharePercentage = 100 - companyRate;

        double userCostPerDay = (lunchPrice * userSharePercentage) / 100;
        double companyCostPerDay = (lunchPrice * companyRate) / 100;
        double userMonthlyCost = userCostPerDay * registeredDays;
        double companyMonthlyCost = companyCostPerDay * registeredDays;
        double estMonthlyCost = lunchPrice * registeredDays;

        LunchDetailsDTO details = new LunchDetailsDTO();
        details.setRegisteredDays(registeredDays);
        details.setLunchPrice(lunchPrice);
        details.setCompanyRate(companyRate);
        details.setUserCostPerDay(userCostPerDay);
        details.setCompanyCostPerDay(companyCostPerDay);
        details.setUserMonthlyCost(userMonthlyCost);
        details.setCompanyMonthlyCost(companyMonthlyCost);
        details.setEstMonthlyCost(estMonthlyCost);

        return details;
    }


    //User
    public Map<String, Object> calculateTotalCostAndDateCountForPreviousWeek(Integer departmentId) throws Exception {
        Calendar calendar = Calendar.getInstance();

        // Get start and end dates of the previous week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        Date startOfPreviousWeek = calendar.getTime();

        calendar.add(Calendar.DATE, 6);
        Date endOfPreviousWeek = calendar.getTime();

        List<UserHasLunch> userHasLunchList;

        if (departmentId != null) {
            // Fetch lunches for a specific department
            userHasLunchList = userHasLunchRepository.findUserHasLunchForPreviousWeekByDepartment(
                    startOfPreviousWeek, endOfPreviousWeek, departmentId
            );
        } else {
            // Fetch lunches for all departments
            userHasLunchList = userHasLunchRepository.findUserHasLunchForPreviousWeek(
                    startOfPreviousWeek, endOfPreviousWeek
            );
        }

        double totalCost = 0;
        long registeredDateCount = 0;

        // Calculate total cost and registered date count
        for (UserHasLunch userHasLunch : userHasLunchList) {
            totalCost += userHasLunch.getUserCost();
            registeredDateCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        result.put("registeredDateCount", registeredDateCount);

        return result;
    }

    public Map<String, Object> calculateTotalCostAndDateCountForMonth(int month, int year, Integer departmentId) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year < 0) {
            throw new IllegalArgumentException("Year must be a positive value.");
        }

        List<UserHasLunch> userHasLunchList;

        if (departmentId == null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForMonth(month, year);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForMonthAndDepartment(month, year, departmentId);
        }

        double totalCost = 0;
        long registeredDateCount = 0;

        // Calculate total cost and count registered dates
        for (UserHasLunch userHasLunch : userHasLunchList) {
            totalCost += userHasLunch.getUserCost();
            registeredDateCount++;
        }

        // Prepare the result
        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        result.put("registeredDateCount", registeredDateCount);

        // Logging for debugging (Optional)
        System.out.println("Total Cost: " + totalCost + ", Registered Date Count: " + registeredDateCount);

        return result;
    }


    public Map<String, Object> calculateTotalCostAndDateCountForYear(int year, Integer departmentId) throws Exception {
        List<UserHasLunch> userHasLunchList;

        if (departmentId == null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForYear(year);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForYearAndDepartment(year, departmentId);
        }

        double totalCost = 0;
        long registeredDateCount = 0;

        for (UserHasLunch userHasLunch : userHasLunchList) {
            totalCost += userHasLunch.getUserCost();
            registeredDateCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        result.put("registeredDateCount", registeredDateCount);

        return result;
    }


    //Admin
    public Map<String, Object> calculateCompanyCostForPreviousWeek( Integer departmentId) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startOfPreviousWeek = calendar.getTime();

        calendar.add(Calendar.DATE, 6);
        Date endOfPreviousWeek = calendar.getTime();

        List<UserHasLunch> userHasLunchList;

        if (departmentId != null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForPreviousWeekByDepartment(
                    startOfPreviousWeek, endOfPreviousWeek, departmentId);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForPreviousWeek(
                    startOfPreviousWeek, endOfPreviousWeek);
        }

        double totalCompanyCost = 0;

        for (UserHasLunch userHasLunch : userHasLunchList) {
            totalCompanyCost += userHasLunch.getCompany_cost();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCompanyCost", totalCompanyCost);

        return result;
    }


    public Map<String, Object> calculateTotalCompanyCostAndDateCountForMonth(int month, int year, Integer departmentId) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year < 0) {
            throw new IllegalArgumentException("Year must be a positive value.");
        }

        List<UserHasLunch> userHasLunchList;

        if (departmentId == null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForMonth(month, year);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForMonthAndDepartment(month, year, departmentId);
        }

        double totalCompanyCost = 0;
        long registeredDateCount = 0;

        for (UserHasLunch userHasLunch : userHasLunchList) {
            if (userHasLunch.getCompany_cost() != null) {
                totalCompanyCost += userHasLunch.getCompany_cost();
            }
            registeredDateCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCompanyCost", totalCompanyCost);
        result.put("registeredDateCount", registeredDateCount);

        System.out.println("Total Company Cost: " + totalCompanyCost + ", Registered Date Count: " + registeredDateCount);

        return result;
    }

    public Map<String, Object> calculateTotalCompanyCostAndDateCountForYear(int year, Integer departmentId) throws Exception {
        List<UserHasLunch> userHasLunchList;

        if (departmentId == null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForYear(year);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForYearAndDepartment(year, departmentId);
        }

        double totalCompanyCost = 0;
        long registeredDateCount = 0;

        for (UserHasLunch userHasLunch : userHasLunchList) {
            if (userHasLunch.getCompany_cost() != null) {
                totalCompanyCost += userHasLunch.getCompany_cost();
            }
            registeredDateCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCompanyCost", totalCompanyCost);
        result.put("registeredDateCount", registeredDateCount);

        return result;
    }
    //Total
    public Map<String, Object> calculateAllTotalCostAndDateCountForPreviousWeek(Integer departmentId) throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);  // Set the day to 1st of the current month
        Date firstDayOfCurrentMonth = calendar.getTime();

        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startOfPreviousWeek = calendar.getTime();

        calendar.add(Calendar.DATE, 6);
        Date endOfPreviousWeek = calendar.getTime();

        List<UserHasLunch> userHasLunchList;

        if (departmentId != null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForPreviousWeekByDepartment(
                    startOfPreviousWeek, endOfPreviousWeek, departmentId);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForPreviousWeek(
                    startOfPreviousWeek, endOfPreviousWeek);
        }

        double totalCost = 0;
        long registeredDateCount = 0;

        for (UserHasLunch userHasLunch : userHasLunchList) {
            totalCost += userHasLunch.getTotal_cost();
            registeredDateCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        result.put("registeredDateCount", registeredDateCount);

        return result;
    }

    public Map<String, Object> calculateAllTotalCostForYear(int year, Integer departmentId) {
        if (year < 0) {
            throw new IllegalArgumentException("Year must be a positive value.");
        }

        double totalCost = 0;
        long registeredDateCount = 0;

        for (int month = 1; month <= 12; month++) {
            List<UserHasLunch> userHasLunchList;

            if (departmentId == null) {
                userHasLunchList = userHasLunchRepository.findUserHasLunchForMonth(month, year);
            } else {
                userHasLunchList = userHasLunchRepository.findUserHasLunchForMonthAndDepartment(month, year, departmentId);
            }

            System.out.println("Month: " + month + " - Number of records: " + userHasLunchList.size());

            for (UserHasLunch userHasLunch : userHasLunchList) {
                System.out.println("User's Total Cost for ID " + userHasLunch.getId() + ": " + userHasLunch.getTotal_cost());
                totalCost += userHasLunch.getTotal_cost();
                registeredDateCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        result.put("registeredDateCount", registeredDateCount);

        System.out.println("Total Cost for Year: " + totalCost + ", Registered Date Count: " + registeredDateCount);

        return result;
    }

    public Map<String, Object> calculateAllTotalCostAndDateCountForMonth(int month, int year, Integer departmentId) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year < 0) {
            throw new IllegalArgumentException("Year must be a positive value.");
        }

        List<UserHasLunch> userHasLunchList;

        if (departmentId == null) {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForMonth(month, year);
        } else {
            userHasLunchList = userHasLunchRepository.findUserHasLunchForMonthAndDepartment(month, year, departmentId);
        }

        double totalCost = 0;
        long registeredDateCount = 0;

        for (UserHasLunch userHasLunch : userHasLunchList) {
            if (userHasLunch.getTotal_cost() != null) {
                totalCost += userHasLunch.getTotal_cost();
            }
            registeredDateCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCost", totalCost);
        result.put("registeredDateCount", registeredDateCount);

        System.out.println("Total Cost: " + totalCost + ", Registered Date Count: " + registeredDateCount);

        return result;
    }

    public Map<String, Object> calculateLunchCostPerDayByUserId(Integer userId) {
        // Fetch the user from the User table
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Fetch the settings (assuming one global settings record is active)
        Settings settings = settingsRepository.findCurrentSettings(); // Customize query to fetch active settings

        if (settings == null) {
            throw new RuntimeException("Settings not found.");
        }

        // Extract data from settings
        double lunchPrice = settings.getCurrentLunchPrice();
        double companyRate = settings.getCompanyRate();
        double userSharePercentage = 100 - companyRate;

        // Calculate the costs
        double userCostPerDay = (lunchPrice * userSharePercentage) / 100;
        double companyCostPerDay = (lunchPrice * companyRate) / 100;

        // Prepare the result
        Map<String, Object> result = new HashMap<>();
        result.put("userName", user.getName()); // Include user details for reference
        result.put("lunchPrice", lunchPrice);
        result.put("userCostPerDay", userCostPerDay);
        result.put("companyCostPerDay", companyCostPerDay);

        return result;
    }



    public Double getCost(Date date) {
        return userHasLunchRepository.getTotalByDate(date);
    }
}
