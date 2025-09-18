package com.AdisApplication.AdisApplication.service;


import com.AdisApplication.AdisApplication.Repository.UserRepository;
import com.AdisApplication.AdisApplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateLoginTime(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            user.setSessionExpiry(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);
        }
    }

    public void extendSession(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setSessionExpiry(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);
        }
    }

    public List<User> getActiveSessions() {
        return userRepository.findActiveSessions(LocalDateTime.now());
    }
}