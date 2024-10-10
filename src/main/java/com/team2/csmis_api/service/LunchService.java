package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.LunchDTO;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.repository.LunchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LunchService {

    @Autowired
    private LunchRepository lunchRepository;

    public List<Lunch> getAllLunches() {
        return lunchRepository.findAll();
    }

    public Optional<Lunch> getLunchById(Integer id) {
        return lunchRepository.findById(id);
    }

    public Lunch createLunch(LunchDTO lunchDTO) {
        Lunch lunch = new Lunch();
        lunch.setMenu(lunchDTO.getMenu());
        lunch.setPrice(lunchDTO.getPrice());
        lunch.setCompanyRate(lunchDTO.getCompanyRate());
        lunch.setDate(lunchDTO.getDate());
        // Set the user and restaurant based on IDs (fetch them using respective services if necessary)
        // lunch.setUser(userService.findById(lunchDTO.getAdminId()).orElse(null));
        // lunch.setRestaurant(restaurantService.findById(lunchDTO.getRestaurantId()).orElse(null));
        return lunchRepository.save(lunch);
    }

    public Lunch updateLunch(Integer id, LunchDTO lunchDTO) {
        Lunch lunch = lunchRepository.findById(id).orElseThrow(() -> new RuntimeException("Lunch not found"));
        lunch.setMenu(lunchDTO.getMenu());
        lunch.setPrice(lunchDTO.getPrice());
        lunch.setCompanyRate(lunchDTO.getCompanyRate());
        lunch.setDate(lunchDTO.getDate());
        // Update user and restaurant if needed
        return lunchRepository.save(lunch);
    }

    public void deleteLunch(Integer id) {
        lunchRepository.deleteLunch(id);
    }
}
