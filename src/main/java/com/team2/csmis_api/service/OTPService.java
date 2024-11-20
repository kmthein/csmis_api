package com.team2.csmis_api.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, OTPDetails> otpStore = new ConcurrentHashMap<>();

    private static final int OTP_VALIDITY_MINUTES = 10;

    public String generateAndStoreOTP(String email) {
        String otp = generateOTP(); // Generate a random 6-digit OTP
        OTPDetails otpDetails = new OTPDetails(otp, LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otpStore.put(email, otpDetails); // Store the OTP and its expiry time
        return otp;
    }

    public boolean validateOTP(String email, String otp) {
        OTPDetails otpDetails = otpStore.get(email); // Retrieve stored OTP details for the email
        if (otpDetails == null) {
            return false;
        }

        if (otpDetails.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }

        return otpDetails.getOtp().equals(otp);
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Generate a random 6-digit number
        return String.valueOf(otp);
    }

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

    public void clearOTP(String email) {
        otpStore.remove(email);
    }
}
