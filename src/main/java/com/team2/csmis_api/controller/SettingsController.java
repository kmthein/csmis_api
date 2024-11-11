package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.SettingsDTO;
import com.team2.csmis_api.entity.Settings;
import com.team2.csmis_api.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(settingsDTO);
        return null;
    }

    @PutMapping("last-register")
    public ResponseDTO setLastRegisterTime(@RequestParam(value = "adminId") Integer adminId, @RequestParam(value = "lastRegisterDay") String lastRegisterDay, @RequestParam(value = "lastRegisterTime") String lastRegisterTime) {
        return settingService.setLastRegister(adminId, lastRegisterDay, lastRegisterTime);
    }
}
