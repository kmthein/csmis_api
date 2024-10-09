package com.team2.csmis_api.controller;

import com.team2.csmis_api.entity.Department;
import com.team2.csmis_api.entity.Division;
import com.team2.csmis_api.entity.Team;
import com.team2.csmis_api.service.StaffDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/")
public class StaffDataController {
    @Autowired
    private StaffDataService staffDataService;

    @GetMapping("divisions")
    public List<Division> getAllDivision() {
        return staffDataService.getAllDivision();
    }

    @GetMapping("departments")
    public List<Department> getAllDepartment() {
        return staffDataService.getAllDepartment();
    }

    @GetMapping("teams")
    public List<Team> getAllTeam() {
        return staffDataService.getAllTeam();
    }
}
