package com.example.bus_ticket_booking.service;

import com.example.bus_ticket_booking.Dto.UserDto;
import com.example.bus_ticket_booking.entity.User;
import com.example.bus_ticket_booking.exception.EmailAlreadyExistsException;
import com.example.bus_ticket_booking.exception.ResourceNotFoundException;
import com.example.bus_ticket_booking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private final ModelMapper modelMapper;

    private PasswordEncoder passwordEncoder;

    public UserDto registerUser(UserDto userDto) {
//        User user = modelMapper.map(userDto,User.class);

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }
        User user = modelMapper.map(userDto, User.class);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        userRepository.deleteById(id);
    }

    public UserDto viewUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole(userDto.getRole());

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public void changePassword(String oldPassword, String newPassword) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new RuntimeException("Old password is incorrect");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User is not authenticated");
        }
    }
}
