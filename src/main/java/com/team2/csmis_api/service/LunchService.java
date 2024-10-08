package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.LunchDTO;
import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.RestaurantRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LunchService {

    @Autowired
    private LunchRepository lunchRepository;

    @Autowired
    private RestaurantRepository restaurantRepository; // Ensure this is injected

    @Autowired
    private UserRepository userRepository;

    public List<Lunch> findAll() {
        return lunchRepository.findAll();
    }

    public Optional<Lunch> findById(Integer id) {
        return lunchRepository.findById(id);
    }

//    public Lunch create(LunchDTO lunchDTO) {
//        Lunch lunch = new Lunch();
//        lunch.setMenu(lunchDTO.getMenu());
//        lunch.setPrice(lunchDTO.getPrice());
//        lunch.setCompanyRate(lunchDTO.getCompanyRate());
//        lunch.setDate(lunchDTO.getDate());
//        // You may need to set User and Restaurant entities here based on IDs
//        return lunchRepository.save(lunch);
//    }

    public Lunch create(LunchDTO lunchDTO) {
        Lunch lunch = new Lunch();
        lunch.setMenu(lunchDTO.getMenu());
        lunch.setPrice(lunchDTO.getPrice());
        lunch.setCompanyRate(lunchDTO.getCompanyRate());
        lunch.setDate(lunchDTO.getDate());

        // Set the restaurant based on the restaurant ID
        if (lunchDTO.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(lunchDTO.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            lunch.setRestaurant(restaurant);
        }

        // Set the user based on the admin ID
        if (lunchDTO.getAdminId() != null) {
            User user = userRepository.findById(lunchDTO.getAdminId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            lunch.setUser(user);
        }

        return lunchRepository.save(lunch);
    }

//    public Lunch update(Integer id, LunchDTO lunchDTO) {
//        Lunch lunch = lunchRepository.findById(id).orElseThrow(() -> new RuntimeException("Lunch not found"));
//        lunch.setMenu(lunchDTO.getMenu());
//        lunch.setPrice(lunchDTO.getPrice());
//        lunch.setCompanyRate(lunchDTO.getCompanyRate());
//        lunch.setDate(lunchDTO.getDate());
//        // You may need to update User and Restaurant entities here
//        return lunchRepository.save(lunch);
//    }

    public Lunch update(Integer id, LunchDTO lunchDTO) {
        Lunch lunch = lunchRepository.findById(id).orElseThrow(() -> new RuntimeException("Lunch not found"));
        lunch.setMenu(lunchDTO.getMenu());
        lunch.setPrice(lunchDTO.getPrice());
        lunch.setCompanyRate(lunchDTO.getCompanyRate());
        lunch.setDate(lunchDTO.getDate());

        // Update restaurant and user as needed
        if (lunchDTO.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(lunchDTO.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            lunch.setRestaurant(restaurant);
        }

        if (lunchDTO.getAdminId() != null) {
            User user = userRepository.findById(lunchDTO.getAdminId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            lunch.setUser(user);
        }

        return lunchRepository.save(lunch);
    }

    public void delete(Integer id) {
        lunchRepository.deleteLunch(id);
    }
}
