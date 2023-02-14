package com.engrveju.Spring.Security6.service.serviceImpl;

import com.engrveju.Spring.Security6.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String emailBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setText(emailBody, true);
//            helper.setFrom("");
            mailSender.send(mimeMessage);
            log.info("Email Successfully sent");
        } catch (MessagingException e) {
            log.error("Failed to send email" +"\n"+  e.getMessage());

        }
    }
}
