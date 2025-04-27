package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwilioService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);
    
    private static final String ACCOUNT_SID = "TWILIO_SID";
    private static final String AUTH_TOKEN = "TWILIO_AUTH_TOKEN";
    private static final String TWILIO_PHONE_NUMBER = "+12708106626";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSMS(String toPhoneNumber, String message) {
        try {
            // Validate and format the phone number
            String formattedNumber = validateAndFormatPhoneNumber(toPhoneNumber);
            if (formattedNumber == null) {
                logger.error("Invalid phone number format: {}", toPhoneNumber);
                return;
            }

            // Check if the number is a short code (5-6 digits)
            if (isShortCode(formattedNumber)) {
                logger.error("Cannot send SMS to short code: {}", formattedNumber);
                return;
            }

            Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    message
            ).create();
            
            logger.info("SMS sent successfully to: {}", formattedNumber);
        } catch (Exception e) {
            logger.error("Failed to send SMS: {}", e.getMessage(), e);
        }
    }

    private static String validateAndFormatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // Remove all non-digit characters
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
        
        // Check if it's a valid length (10 digits for US numbers, adjust for other countries)
        if (digitsOnly.length() < 10 || digitsOnly.length() > 15) {
            return null;
        }

        // Format as E.164
        return "+" + digitsOnly;
    }

    private static boolean isShortCode(String phoneNumber) {
        // Remove all non-digit characters and check length
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
        return digitsOnly.length() <= 6;
    }
} 