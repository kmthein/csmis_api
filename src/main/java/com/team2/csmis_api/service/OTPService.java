package com.team2.csmis_api.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    // ConcurrentHashMap to store OTP and its expiry time for each email
    private final Map<String, OTPDetails> otpStore = new ConcurrentHashMap<>();

    // OTP validity time in minutes
    private static final int OTP_VALIDITY_MINUTES = 10;

    // Generate and store OTP for the given email
    public String generateAndStoreOTP(String email) {
        String otp = generateOTP(); // Generate a random 6-digit OTP
        OTPDetails otpDetails = new OTPDetails(otp, LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otpStore.put(email, otpDetails); // Store the OTP and its expiry time
        return otp;
    }

    // Validate the OTP for the given email
    public boolean validateOTP(String email, String otp) {
        OTPDetails otpDetails = otpStore.get(email); // Retrieve stored OTP details for the email
        if (otpDetails == null) {
            return false; // No OTP found for the email
        }

        // Check if the OTP has expired
        if (otpDetails.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpStore.remove(email); // Remove expired OTP
            return false; // OTP is expired
        }

        // Check if the provided OTP matches the stored OTP
        return otpDetails.getOtp().equals(otp);
    }

    // Generate a random 6-digit OTP
    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Generate a random 6-digit number
        return String.valueOf(otp);
    }

    // Inner class to store OTP and its expiry time
    private static class OTPDetails {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OTPDetails(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }

    // Clear OTP for the given email after successful validation or expiration
    public void clearOTP(String email) {
        otpStore.remove(email);
    }
}
