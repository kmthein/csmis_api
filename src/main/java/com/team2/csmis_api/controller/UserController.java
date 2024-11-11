package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.DietaryPreferenceDTO;
import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.Holiday;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/admin/api/users")
//@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("excel")
    public ResponseEntity<?> uploadStaffData(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "adminId", required = false) Integer adminId) throws IOException {
        List<User> users = userService.saveUserToDatabase(file, adminId);
        if(users.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Employee data updated successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Something went wrong"));
        }
    }

    @PostMapping("")
    public ResponseDTO addNewUser(@ModelAttribute UserDTO userDTO) {
        return userService.addNewUser(userDTO);
    }

    @PutMapping("{id}")
    public ResponseDTO updateUserById(@PathVariable("id") Integer id, @ModelAttribute UserDTO userDTO) {
        return userService.updateUserById(userDTO, id);
    }

    @GetMapping("")
    public List<UserDTO> getAllActiveUsers() {
        return userService.getAllActiveUsers();
    }

    @GetMapping("{id}")
    public UserDTO getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }
    @PostMapping("/saveDietaryPreference")
    public ResponseEntity<?> saveDietaryPreference(@RequestBody DietaryPreferenceDTO preferenceDTO) {
        userService.updateDietaryPreference(preferenceDTO);
        return ResponseEntity.ok("Dietary preferences updated successfully.");
    }
}
