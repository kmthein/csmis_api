package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.Lunch;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.entity.UserHasLunch;
import com.team2.csmis_api.repository.LunchRepository;
import com.team2.csmis_api.repository.UserHasLunchRepository;
import com.team2.csmis_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserHasLunchServicesTest {

    @InjectMocks
    private UserHasLunchServices userHasLunchServices;

    @Mock
    private UserHasLunchRepository userHasLunchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LunchRepository lunchRepository;

    private User user;
    private Lunch lunch;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setName("John Doe");

        lunch = new Lunch();
        lunch.setId(1);
        lunch.setMenu("Lunch Menu");
        lunch.setDate(LocalDate.now());
    }

    @Test
    public void testRegisterUserForLunch_Success() throws Exception {
        // Arrange
        Date date = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(lunchRepository.findByDate(LocalDate.now())).thenReturn(Optional.of(lunch));

        // Act
        userHasLunchServices.registerUserForLunch(1, List.of(date));

        // Assert
        verify(userHasLunchRepository, times(1)).save(any(UserHasLunch.class));
    }

    @Test
    public void testRegisterUserForLunch_UserNotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            userHasLunchServices.registerUserForLunch(1, List.of(new Date()));
        } catch (Exception e) {
            assert e.getMessage().equals("User not found with id: 1");
        }
    }

    @Test
    public void testGetDtByUserId() {
        // Arrange
        List<Date> dates = List.of(new Date());
        when(userHasLunchRepository.findDtByUserId(1)).thenReturn(dates);

        // Act
        List<Date> result = userHasLunchServices.getDtByUserId(1);

        // Assert
        assert result.equals(dates);
    }

    @Test
    public void testDeleteLunchRegistrations() {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        UserHasLunch registration = new UserHasLunch();
        registration.setDt(Date.from(currentDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));  // Set date to today

        List<UserHasLunch> registrations = List.of(registration);
        when(userHasLunchRepository.findByUserId(1)).thenReturn(registrations);

        // Act
        userHasLunchServices.deleteLunchRegistrations(1);

        // Assert
        // Verifying that the `delete` method is called once with the exact object (registration)
        verify(userHasLunchRepository, times(1)).delete(registration);
    }
}


