package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.SettingsDTO;
import com.team2.csmis_api.entity.Settings;
import com.team2.csmis_api.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/settings")
public class SettingsController {
    @Autowired
    private SettingService settingService;

    @GetMapping("")
    public Settings getSettings() {
        return settingService.getSettings();
    }

    @PutMapping("")
    public ResponseDTO updateSettings(@ModelAttribute SettingsDTO settingsDTO) {
        return settingService.updateSettings(settingsDTO);
    }

    @PutMapping("last-register")
    public ResponseDTO setLastRegisterTime(@RequestParam(value = "adminId") Integer adminId, @RequestParam(value = "lastRegisterDay") String lastRegisterDay, @RequestParam(value = "lastRegisterTime") String lastRegisterTime) {
        return settingService.setLastRegister(adminId, lastRegisterDay, lastRegisterTime);
    }
//    @GetMapping("/isRegistrationAllowed")
//    public ResponseEntity<?> getRegistrationStatus() {
//        boolean allowed = settingService.isRegistrationAllowed();
//        Settings settings = settingService.getLatestSettings(); // Fetch the latest settings
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("allowed", allowed);
//        response.put("lastRegisterDay", settings.getLastRegisterDay().toString());
//        response.put("lastRegisterTime", settings.getLastRegisterTime().toString());
//
//        return ResponseEntity.ok(response);
@GetMapping("/registration-cutoff")
public Settings getRegistrationCutoff() {
    return settingService.getRegistrationCutoff();
}
//    }
}
