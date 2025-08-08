package com.payrollsystem.notification_service.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    /**
     * Initializes Twilio client when the service is constructed.
     * Ensures that Twilio.init() is called once.
     */
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        logger.info("Twilio client initialized.");
    }

    /**
     * Sends an SMS message using Twilio.
     *
     * @param toPhoneNumber The recipient's phone number (e.g., +1234567890).
     * @param smsBody The content of the SMS message.
     */
    public void sendSms(String toPhoneNumber, String smsBody) {
        try {
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber),  // To
                            new PhoneNumber(twilioPhoneNumber), // From (your Twilio number)
                            smsBody)
                    .create();
            logger.info("SMS sent successfully to {} with SID: {}", toPhoneNumber, message.getSid());
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}. Error: {}", toPhoneNumber, e.getMessage(), e);
        }
    }
}