package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.RestaurantDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.Restaurant;
import com.team2.csmis_api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public RestaurantDTO getRestaurantById(@PathVariable Integer id) {
        return restaurantService.findById(id);

    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        try {
            restaurantService.save(restaurantDTO);
            return ResponseEntity.ok().body(Map.of("message", "Restaurant created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error occurred while creating restaurant"));
        }
    }

    @PutMapping("{id}")
    public ResponseDTO updateRestaurantById(@PathVariable("id") Integer id, @ModelAttribute RestaurantDTO restaurantDTO) {
        return restaurantService.updateRestaurantById(restaurantDTO,id);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
