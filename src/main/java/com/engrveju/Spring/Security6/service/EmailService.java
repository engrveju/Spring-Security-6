package com.engrveju.Spring.Security6.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void send(String to, String emailContent);
}
