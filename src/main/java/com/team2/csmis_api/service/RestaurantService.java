package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.RestaurantDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.RestaurantRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import com.team2.csmis_api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RestaurantService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ModelMapper mapper;

    public List<RestaurantDTO> getAllRestaurants() {
        List<Restaurant> restaurant = restaurantRepository.getAllRestaurants();
        List<RestaurantDTO> restaurantDTOList = new ArrayList<>();

        for(Restaurant r: restaurant) {
            RestaurantDTO restaurantDTO = mapRestaurantToDTO(r);
            restaurantDTOList.add(restaurantDTO);
        }
        return restaurantDTOList;
    }

    public RestaurantDTO mapRestaurantToDTO(Restaurant restaurant) {
        RestaurantDTO restaurantDTO = mapper.map(restaurant, RestaurantDTO.class);
        if(restaurant.getName() != null) {
            restaurantDTO.setName(restaurant.getName());
        }
        if(restaurant.getAddress() != null) {
            restaurantDTO.setAddress(restaurant.getAddress());
        }
        if(restaurant.getContact() != null) {
            restaurantDTO.setContact(restaurant.getContact());
        }
        if(restaurant.getIsActive() == true) {
            restaurantDTO.setStatus(Status.Active.toString());
        } else {
            restaurantDTO.setStatus(Status.InActive.toString());
        }
        return restaurantDTO;
    }

    public RestaurantDTO findById(int id) {
        Restaurant restaurant=restaurantRepository.getRestaurantById(id);
        RestaurantDTO restaurantDTO = mapRestaurantToDTO(restaurant);
        return restaurantDTO;
    }

    public Restaurant save(RestaurantDTO restaurantDTO) {
        User adminId = userRepo.findById(restaurantDTO.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDTO.getName());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setContact(restaurantDTO.getContact());
        restaurant.setEmail(restaurantDTO.getEmail());
        restaurant.setUser(adminId);
        return restaurantRepository.save(restaurant);
    }


    public ResponseDTO updateRestaurantById(RestaurantDTO restaurantDTO, int id) {
        Restaurant temRestaurant = restaurantRepository.findById(id).get();
        ResponseDTO res = new ResponseDTO();
        if(temRestaurant == null) {
            throw new ResourceNotFoundException("Restaurant not existed with this id.");
        }
        temRestaurant .setName(restaurantDTO.getName());
        temRestaurant .setAddress(restaurantDTO.getAddress());
        temRestaurant .setContact(restaurantDTO.getContact());
        temRestaurant .setEmail(restaurantDTO.getEmail());
        if(temRestaurant.getIsActive() == restaurantDTO.getStatus().equals("Active")) {
            temRestaurant.setIsActive(true);
        } else {
            temRestaurant.setIsActive(false);
        }
        Restaurant restaurantSave = restaurantRepository.save(temRestaurant);
        if(temRestaurant != null) {
            res.setStatus("200");
            res.setMessage("Restaurant data updated successfully.");
        } else {
            res.setStatus("403");
            res.setMessage("Restaurant data update failed.");
        }
        return res;
    }

    public void deleteRestaurant(Integer id) {
        restaurantRepository.deleteRestaurant(id);
    }


}
