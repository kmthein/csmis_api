package com.team2.csmis_api.controller;

import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.UserRepository;
import com.team2.csmis_api.service.EmailService;
import com.team2.csmis_api.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ForgotPasswordController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        // Generate and store OTP
        String otp = otpService.generateAndStoreOTP(email);

        // Send OTP via email
        String subject = "Password Reset Request";
        String message = "To reset your password, use this OTP: " + otp;
        emailService.sendSimpleMessage(email, subject, message);

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<Map<String, String>> validateOTP(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.validateOTP(email, otp);
        Map<String, String> response = new HashMap<>();

        if (isValid) {
            response.put("message", "OTP is valid");
            return ResponseEntity.ok(response);  // Return JSON response with status 200
        } else {
            response.put("message", "Invalid or expired OTP");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Return JSON response with status 401
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String email,
            @RequestParam String newPassword) { // Removed OTP parameter

        System.out.println("Received request to update password for email: " + email);

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            System.out.println("Email not found: " + email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
        }

        User user = userOptional.get();
        System.out.println("User found: " + user.getName());

        // Encode and update the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        System.out.println("Password updated for user: " + user.getName());

        return ResponseEntity.ok("Password updated successfully!");
    }



    private String generateResetToken() {
        int token = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(token);
    }

}
