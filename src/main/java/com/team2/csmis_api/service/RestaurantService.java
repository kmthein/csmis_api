package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.RestaurantDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.repository.RestaurantRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.getAllRestaurants();
    }

    public Optional<Restaurant> findById(Integer id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant save(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDTO.getName());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setContact(restaurantDTO.getContact());
        restaurant.setEmail(restaurantDTO.getEmail());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant update(Integer id, RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setName(restaurantDTO.getName());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setContact(restaurantDTO.getContact());
        restaurant.setEmail(restaurantDTO.getEmail());
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Integer id) {
        restaurantRepository.deleteRestaurant(id);
    }


}
