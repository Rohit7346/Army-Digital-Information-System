package com.AdisApplication.AdisApplication.service;

import com.AdisApplication.AdisApplication.Repository.AdminRepository;
import com.AdisApplication.AdisApplication.entity.admin;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultAdmin() {
        // Create default admin if not exists
        if (adminRepository.findByUsername("admin").isEmpty()) {
            admin admin = new admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
        }
    }

    public Optional<admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public boolean validateAdmin(String username, String password) {
        Optional<admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            return passwordEncoder.matches(password, adminOpt.get().getPassword());
        }
        return false;
    }
}