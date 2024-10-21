package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.Department;
import com.team2.csmis_api.entity.Division;
import com.team2.csmis_api.entity.Team;

import java.util.List;

public interface StaffDataService {
    List<Division> getAllDivision();
    List<Department> getAllDepartment();
    List<Team> getAllTeam();
}
