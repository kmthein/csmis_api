package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.MeatDTO;
import com.team2.csmis_api.entity.Meat;
import com.team2.csmis_api.repository.MeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeatService {
    @Autowired
    private MeatRepository meatRepository;

    public List<Meat> findAll() {
        return meatRepository.findAllByIsDeletedFalse();
    }

    public Optional<Meat> findById(Integer id) {
        return meatRepository.findById(id);
    }

    public Meat save(Meat meat) {
        return meatRepository.save(meat);
    }

    public void deleteById(Integer id) {
        meatRepository.deleteById(id);
    }

    public boolean isNameUnique(String name) {
        return meatRepository.findAllActiveMeats().stream()
                .noneMatch(meat -> meat.getName().equalsIgnoreCase(name));
    }




    public void softDeleteMeat(Integer id) {
        Optional<Meat> meatOpt = meatRepository.findById(id);

        if (meatOpt.isPresent()) {
            Meat meat = meatOpt.get();
            meat.setIsDeleted(true); // Set the isDeleted flag to true
            meatRepository.save(meat); // Save the updated entity
        } else {
            throw new RuntimeException("Meat not found");
        }
    }

    public boolean isNameUniqueUpdate(String name, Integer id) {
        return !meatRepository.existsByNameIgnoreCaseAndIdNot(name, id);

    }

}
