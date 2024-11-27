package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Suggestion;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHasSuggestionRepository extends JpaRepository<UserHasSuggestion, Integer> {

    @Query("SELECT uhs FROM UserHasSuggestion uhs WHERE uhs.user.id = :userId ORDER BY uhs.suggestion.createdAt DESC")
    List<UserHasSuggestion> findByIsSeenFalseAndUser_Id(Integer userId);

    @Query("SELECT uhs FROM UserHasSuggestion uhs ORDER BY uhs.suggestion.createdAt DESC") // Assuming suggestion has a date field
    List<UserHasSuggestion> findLatestSuggestions();

    UserHasSuggestion findBySuggestionAndUser(Suggestion suggestion, User user);
}
