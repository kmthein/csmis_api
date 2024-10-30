package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.RestaurantDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Integer id) {
        return restaurantService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Restaurant createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        return restaurantService.save(restaurantDTO);
    }

    @PutMapping("/{id}")
    public Restaurant updateRestaurant(@PathVariable Integer id, @RequestBody RestaurantDTO restaurantDTO) {
        return restaurantService.update(id, restaurantDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
