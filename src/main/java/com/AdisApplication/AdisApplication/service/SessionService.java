package com.AdisApplication.AdisApplication.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    // This service can be extended to handle session cleanup tasks
    // Currently, sessions are managed via JWT expiration and database timestamps

    // You could add scheduled tasks here to clean up expired sessions
}