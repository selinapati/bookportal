package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTestFailure {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    @Test
    public void testSaveUserFailure() {
        // Mock the password encoding process
        when(passwordEncoder.encode("password123")).thenReturn("encryptedPassword");

        // Simulate a failure by throwing an exception during user saving
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

        // Assert that a DataIntegrityViolationException is thrown
        assertThrows(DataIntegrityViolationException.class, () -> {
            userDetailsService.saveUser(user);
        });

        // Verify that userRepository's save was attempted
        verify(userRepository).save(user);
    }
}
