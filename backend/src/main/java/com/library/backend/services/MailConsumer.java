package com.library.backend.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailConsumer {

    MailService emailService;

    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void consume(String message) {
        try {
            String[] parts = message.split("\\|", 3);
            String to = parts[0];
            String subject = parts[1];
            String content = parts[2];

            emailService.sendTextEmail(to, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
            // production: log hoặc retry
        }
    }
}
