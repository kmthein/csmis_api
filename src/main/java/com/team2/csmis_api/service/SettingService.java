package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.entity.Settings;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.SettingRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private UserRepository userRepository;

    public Settings getSettings() {
        return settingRepository.findById(1).get();
    }

    public ResponseDTO setLastRegister(Integer adminId, String day, String time) {
        ResponseDTO res = new ResponseDTO();
        Optional<Settings> optionalSettings = settingRepository.findById(1);
        Optional<User> optAdmin = userRepository.findById(adminId);
        User admin = new User();
        if(optAdmin.isPresent()) {
            admin = optAdmin.get();
        }
        if(optionalSettings.isEmpty()) {
            Settings newSetting = new Settings();
            newSetting.setId(1);
            newSetting.setLastRegisterDay(day);
            newSetting.setLastRegisterTime(time);
            newSetting.setAdmin(admin);
            settingRepository.save(newSetting);
        } else {
            Settings tempSetting = optionalSettings.get();
            tempSetting.setLastRegisterDay(day);
            tempSetting.setLastRegisterTime(time);
            tempSetting.setAdmin(admin);
            Settings settings = settingRepository.save(tempSetting);
            if(settings != null) {
                res.setStatus("200");
                res.setMessage("Register time updated");
            } else {
                res.setStatus("401");
                res.setMessage("Something went wrong");
            }
        }
        return res;
    }
}
