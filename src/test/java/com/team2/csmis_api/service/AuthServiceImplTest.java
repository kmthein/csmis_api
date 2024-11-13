package com.team2.csmis_api.service;
import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.dto.ResponseTokenDTO;
import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ModelMapper mapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    void testRegister_NewUser_ShouldReturnSuccess() {
        User user = new User();
        user.setStaffId("S123");
        user.setPassword("password");

        // Mock the behavior of userRepo.findByStaffId to return null, indicating user doesn't exist
        when(userRepo.findByStaffId("S123")).thenReturn(null);
        // Mock password encoding
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Call the register method
        ResponseDTO response = authService.register(user);

        // Assert the response status and message
        assertEquals("201", response.getStatus());
        assertEquals("User Registration Successful", response.getMessage());

        // Verify that save was called once on the userRepo
        verify(userRepo, times(1)).save(any(User.class));
    }


    @Test
    void testLogin_ValidCredentials_ShouldReturnToken() {
        User user = new User();
        user.setStaffId("S123");
        user.setPassword("password");

        when(userRepo.findByStaffId("S123")).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        Object response = authService.login(user);

        assertTrue(response instanceof ResponseTokenDTO);
        assertEquals("jwtToken", ((ResponseTokenDTO) response).getToken());
    }

    @Test
    void testLogin_InvalidCredentials_ShouldReturnError() {
        User user = new User();
        user.setStaffId("S123");
        user.setPassword("wrongPassword");

        when(userRepo.findByStaffId("S123")).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));

        Object response = authService.login(user);

        assertTrue(response instanceof ResponseDTO);
        assertEquals("401", ((ResponseDTO) response).getStatus());
        assertEquals("Password is incorrect, try again", ((ResponseDTO) response).getMessage());
    }
}
