package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Suggestion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {

    @Query("SELECT s FROM Suggestion s WHERE s.isDeleted = false")
    public List<Suggestion> getAllSuggestions();

    @Modifying
    @Transactional
    @Query("UPDATE Suggestion s SET s.isDeleted=true WHERE s.id=?1")
    public void deleteSuggestion(Integer id);
}
