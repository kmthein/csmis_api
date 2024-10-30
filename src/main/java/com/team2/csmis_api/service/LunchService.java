package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.LunchDTO;
import com.team2.csmis_api.dto.MenuDTO;
import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.WeeklyMenuDTO;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.RestaurantRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LunchService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LunchRepository lunchRepository;

    // Method to find all lunches and return as DTOs
    public List<LunchDTO> findAll() {
        List<Lunch> lunches = lunchRepository.findAll();
        System.out.println("Retrieved lunches: " + lunches); // Debugging line
        return lunches.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LunchDTO> getCurrentWeekMenu() {
        List<Lunch> lunchList = lunchRepository.getCurrentWeekMenu();
        if(lunchList.size() > 0) {
            return lunchList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public ResponseDTO addWeeklyMenu(WeeklyMenuDTO weeklyMenuDTO) {
        ResponseDTO res = new ResponseDTO();
        List<Lunch> lunchList = new ArrayList<>();
        for(MenuDTO menuDTO : weeklyMenuDTO.getMenuList()) {
            Lunch lunch = new Lunch();
            lunch.setMenu(menuDTO.getMenu());
            lunch.setDate(menuDTO.getDate());
            lunch.setPrice(weeklyMenuDTO.getPrice());
            lunch.setCompanyRate((double) weeklyMenuDTO.getRate());
            Optional<Restaurant> optRestaurant = restaurantRepository.findByName(weeklyMenuDTO.getRestaurant());
            Optional<User> optAdmin = userRepository.findById(weeklyMenuDTO.getAdminId());
            optRestaurant.ifPresent(lunch::setRestaurant);
            optAdmin.ifPresent(lunch::setUser);
            lunchRepository.save(lunch);
            lunchList.add(lunch);
        }
        if(lunchList.size() > 0) {
            res.setMessage("Lunch added successfully");
            res.setStatus("201");
        } else {
            res.setMessage("Lunch can't added");
            res.setStatus("401");
        }
        return res;
    }

    // Method to save a lunch and return as DTO
    public LunchDTO save(LunchDTO lunchDTO) {
        Lunch lunch = convertToEntity(lunchDTO);
        Lunch savedLunch = lunchRepository.save(lunch);
        return convertToDTO(savedLunch);
    }

    // Mapping entity to DTO
    private LunchDTO convertToDTO(Lunch lunch) {
        LunchDTO dto = new LunchDTO();
        dto.setId(lunch.getId());
        dto.setMenu(lunch.getMenu());
        dto.setPrice(lunch.getPrice());
        dto.setCompanyRate(lunch.getCompanyRate());
        dto.setDate(lunch.getDate());
        if (lunch.getUser() != null) {
            dto.setAdminId(lunch.getUser().getId());
        } else {
            dto.setAdminId(null); // or handle accordingly
        }

        if (lunch.getRestaurant() != null) {
            dto.setRestaurantId(lunch.getRestaurant().getId());
            dto.setRestaurantName(lunch.getRestaurant().getName());
        } else {
            dto.setRestaurantId(null); // or handle accordingly
        }
        return dto;
    }

    // Mapping DTO to entity
    private Lunch convertToEntity(LunchDTO dto) {
        Lunch lunch = new Lunch();
        lunch.setId(dto.getId());
        lunch.setMenu(dto.getMenu());
        lunch.setPrice(dto.getPrice());
        lunch.setCompanyRate(dto.getCompanyRate());
        lunch.setDate(dto.getDate());

        // Set User and Restaurant based on IDs
        if (dto.getAdminId() != null) {
            User user = userRepository.findById(dto.getAdminId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            lunch.setUser(user);
        }

        if (dto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            lunch.setRestaurant(restaurant);
        }

        return lunch;
    }

    public LunchDTO getLunchById(int id) {
        Optional<Lunch> optionalLunch = lunchRepository.findById(id);
        LunchDTO lunchDTO = new LunchDTO();
        if(optionalLunch.isPresent()) {
            Lunch lunch = optionalLunch.get();
            lunchDTO = convertToDTO(lunch);
        }
        return lunchDTO;
    }

    public LunchDTO updateLunch(Integer id, LunchDTO lunchDTO) {
        Lunch existingLunch = lunchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lunch not found"));

        // Update fields
        existingLunch.setMenu(lunchDTO.getMenu());
        existingLunch.setPrice(lunchDTO.getPrice());
        existingLunch.setCompanyRate(lunchDTO.getCompanyRate());
        if(lunchDTO.getDate() != null) {
            existingLunch.setDate(lunchDTO.getDate());
        }
        // Optionally update User and Restaurant if IDs are provided
        if (lunchDTO.getAdminId() != null) {
            User user = userRepository.findById(lunchDTO.getAdminId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existingLunch.setUser(user);
        }

        if (lunchDTO.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(lunchDTO.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            existingLunch.setRestaurant(restaurant);
        }

        Lunch updatedLunch = lunchRepository.save(existingLunch);
        return convertToDTO(updatedLunch);
    }

    public void deleteLunch(Integer id) {
        lunchRepository.deleteLunch(id);
    }
}
