package com.library.backend.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailProducer {

//    KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "email-topic";

    public void sendEmailMessage(String to, String subject, String content) {
        String msg = to + "|" + subject + "|" + content; // serialize đơn giản
//        kafkaTemplate.send(TOPIC, msg);
    }
}