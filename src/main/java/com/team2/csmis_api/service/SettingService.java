package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.entity.Settings;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepository;
    public Settings getSettings() {
        return settingRepository.findById(1).get();
    }

    public ResponseDTO setLastRegister(String day, String time) {
        ResponseDTO res = new ResponseDTO();
        Settings tempSetting = settingRepository.findById(1).get();
        if(tempSetting == null) {
            throw new ResourceNotFoundException("Setting not found");
        } else {
            tempSetting.setLastRegisterDay(day);
            tempSetting.setLastRegisterTime(time);
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
