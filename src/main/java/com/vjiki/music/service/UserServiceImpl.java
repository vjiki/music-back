package com.vjiki.music.service;

import com.vjiki.music.dto.AuthRequest;
import com.vjiki.music.dto.AuthResponse;
import com.vjiki.music.dto.UserResponse;
import com.vjiki.music.entity.User;
import com.vjiki.music.mapper.UserMapper;
import com.vjiki.music.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, 
                          UserMapper userMapper,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<User> userOpt = userRepository.findByEmail(authRequest.getEmail());
        
        if (userOpt.isEmpty()) {
            return new AuthResponse(false, null, "Invalid email or password");
        }
        
        User user = userOpt.get();
        
        // Check if user is active
        if (Boolean.FALSE.equals(user.getIsActive())) {
            return new AuthResponse(false, null, "User account is inactive");
        }
        
        // Check password if user has a password hash (LOCAL provider)
        if (user.getProvider() == com.vjiki.music.entity.AuthProvider.LOCAL) {
            if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                return new AuthResponse(false, null, "Password not set for this user");
            }
            
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
                return new AuthResponse(false, null, "Invalid email or password");
            }
        } else {
            // For OAuth providers, password authentication is not applicable
            return new AuthResponse(false, null, "This account uses OAuth authentication");
        }
        
        return new AuthResponse(true, user.getId(), "Authentication successful");
    }
}

