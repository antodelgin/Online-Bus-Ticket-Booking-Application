package com.example.bus_ticket_booking;

import com.example.bus_ticket_booking.Dto.UserDto;
import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.exception.EmailAlreadyExistsException;
import com.example.bus_ticket_booking.exception.ResourceNotFoundException;
import com.example.bus_ticket_booking.repository.UserRepository;
import com.example.bus_ticket_booking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setPassword("hashedPassword");
        user.setRole(User.Role.valueOf("USER"));

        userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setPhoneNumber("1234567890");
        userDto.setPassword("password");
        userDto.setRole(User.Role.valueOf("USER"));
    }

    @Test
    void registerUser_Success() {
        userDto.setPassword("password");
        User mockUser = new User();
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(modelMapper.map(userDto, User.class)).thenReturn(mockUser);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            return savedUser;
        });

        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserDto(user.getName(), user.getEmail(), user.getPassword());
        });

        UserDto registeredUser = userService.registerUser(userDto);

        assertNotNull(registeredUser);
        assertEquals("Test User", registeredUser.getName());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("hashedPassword", registeredUser.getPassword());

        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.registerUser(userDto);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void existsByEmail_True() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_False() {
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("nonexistent@example.com");

        assertFalse(result);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void viewUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto viewedUser = userService.viewUser(1L);

        assertNotNull(viewedUser);
        assertEquals("Test User", viewedUser.getName());
        assertEquals("test@example.com", viewedUser.getEmail());
    }

    @Test
    void viewUser_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.viewUser(999L);
        });
    }

    @Test
    void updateUser_Success() {
        UserDto updatedDto = new UserDto();
        updatedDto.setName("Updated User");
        updatedDto.setEmail("updated@example.com");
        updatedDto.setPhoneNumber("9876543210");
        updatedDto.setRole(User.Role.valueOf("ADMIN"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(updatedDto);

        UserDto result = userService.updateUser(1L, updatedDto);

        assertNotNull(result);
        assertEquals("Updated User", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("9876543210", result.getPhoneNumber());
        assertEquals(User.Role.ADMIN, result.getRole());

        assertEquals("Updated User", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("9876543210", user.getPhoneNumber());
        assertEquals(User.Role.ADMIN, user.getRole());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(999L, userDto);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
    }

    @Test
    void getUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail("nonexistent@example.com");
        });
    }

    @Test
    void changePassword_Success() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        userService.changePassword("oldPassword", "newPassword");

        assertEquals("newHashedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_IncorrectOldPassword() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", "hashedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword("wrongOldPassword", "newPassword");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_UserNotAuthenticated() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword("oldPassword", "newPassword");
        });
    }
}