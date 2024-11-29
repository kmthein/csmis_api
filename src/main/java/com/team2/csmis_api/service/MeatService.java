package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.Meat;
import com.team2.csmis_api.repository.MeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Meat existMeat = meatRepository.findByName(meat.getName());
        if(existMeat != null) {
            existMeat.setIsDeleted(false);
            return meatRepository.save(existMeat);
        }
        return meatRepository.save(meat);
    }

    public void deleteById(Integer id) {
        meatRepository.deleteById(id);
    }
    public boolean isNameUnique(String name) {
        String normalizedName = name.trim().replaceAll("\\s", "").toLowerCase(); // Normalize name
        return meatRepository.isNameUnique(normalizedName);
    }


//    public boolean isNameUnique(String name, Integer idToExclude) {
//        String normalizedName = name.trim().toLowerCase();
//        long count = meatRepository.countByNormalizedNameExcludingId(normalizedName, idToExclude);
//        return count == 0; // Return true if no such name exists
//    }





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
