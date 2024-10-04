package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws URISyntaxException {
        ResponseDTO res = new ResponseDTO();
        if(user.getPassword().length() < 6) {
            res.setStatus("403");
            res.setMessage("Password must have between 6 and 20 characters");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        } else if(user.getPassword().length() > 20) {
            res.setStatus("403");
            res.setMessage("Password must have between 6 and 20 characters");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        res = authService.register(user);
        if(!res.getStatus().equals("201")) {
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        }
        URI uri = new URI("/api/login");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Object login(@RequestBody User user) {
        return authService.login(user);
    }
}
