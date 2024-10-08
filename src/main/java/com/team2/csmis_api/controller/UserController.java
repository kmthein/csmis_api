package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.UserDTO;
import com.team2.csmis_api.entity.Holiday;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<?> uploadStaffData(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "adminId", required = false) Integer adminId) throws IOException {

        System.out.println("File received: " + file.getOriginalFilename());
        System.out.println("Admin ID: " + adminId);
        List<User> users = userService.saveUserToDatabase(file, adminId);
        if(users.size() > 0) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("Message", "Employee data uploaded and saved to database successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Message", "Something went wrong"));
        }
    }

    @GetMapping("")
    public List<UserDTO> getAllActiveUsers() {
        return userService.getAllActiveUsers();
    }

    @GetMapping("{id}")
    public UserDTO getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }
}
