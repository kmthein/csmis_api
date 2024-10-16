package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.HolidayDTO;
import com.team2.csmis_api.entity.Holiday;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.HolidayRepository;
import com.team2.csmis_api.repository.UserRepository;
import com.team2.csmis_api.util.ExcelOfHolidayExportUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HolidayService {
    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    public void saveHolidayToDatabase(MultipartFile file,Holiday holiday){
        if(ExcelForHolidayService.isValidExcelFile(file)){
            try {
                List<Holiday> holidays = ExcelForHolidayService.getHolidaysDataFromExcel(file.getInputStream());
                User adminId = userRepository.findById(holiday.getUser().getId()).orElseThrow(() ->
                        new IllegalArgumentException("Admin not found")
                );

                for (Holiday h : holidays) {
                    h.setUser(adminId);
                }

                this.holidayRepository.saveAll(holidays);
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        }
    }

    public HolidayDTO convertToDto(Holiday holiday) {
        return modelMapper.map(holiday, HolidayDTO.class);
    }

    public Holiday convertToEntity(HolidayDTO holidayDTO) {
        return modelMapper.map(holidayDTO, Holiday.class);
    }

    public List<HolidayDTO> showAllHoliday() {
        return holidayRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<HolidayDTO> showByHolidayId(Integer id) {
        return holidayRepository.findById(id)
                .map(this::convertToDto);
    }

    public HolidayDTO updateHoliday(Integer id, HolidayDTO holidayDTO) {
        Holiday existingHoliday = holidayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found with ID: " + id));
        modelMapper.map(holidayDTO,existingHoliday);
        Holiday updatedHoliday = holidayRepository.save(existingHoliday);
        return convertToDto(updatedHoliday);
    }

    public void deleteHoliday(Integer id){
        holidayRepository.deleteHoliday(id);
    }


    public List<Holiday> exportHolidayToExcel(HttpServletResponse response) throws IOException {
        List<Holiday> holidays = holidayRepository.findAll();
        ExcelOfHolidayExportUtils exportUtils = new ExcelOfHolidayExportUtils(holidays);
        exportUtils.exportDataToExcel(response);
        return holidays;
    }

}
