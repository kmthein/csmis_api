package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.UserHasSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHasSuggestionRepository extends JpaRepository<UserHasSuggestion, Integer> {

    List<UserHasSuggestion> findByIsSeenFalseAndUser_Id(Integer userId);

    @Query("SELECT uhs FROM UserHasSuggestion uhs ORDER BY uhs.suggestion.date DESC") // Assuming suggestion has a date field
    List<UserHasSuggestion> findLatestSuggestions();
}
