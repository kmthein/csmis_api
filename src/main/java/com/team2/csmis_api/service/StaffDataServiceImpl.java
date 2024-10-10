package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.Department;
import com.team2.csmis_api.entity.Division;
import com.team2.csmis_api.entity.Team;
import com.team2.csmis_api.repository.DepartmentRepository;
import com.team2.csmis_api.repository.DivisionRepository;
import com.team2.csmis_api.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffDataServiceImpl implements StaffDataService {
    @Autowired
    private DivisionRepository divisionRepo;

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Override
    public List<Division> getAllDivision() {
        return divisionRepo.findAll();
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentRepo.findAll();
    }

    @Override
    public List<Team> getAllTeam() {
        return teamRepo.findAll();
    }
}
