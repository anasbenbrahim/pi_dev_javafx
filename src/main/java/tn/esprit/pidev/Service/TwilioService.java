package tn.esprit.pidev.Service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {
    // Remplacez par vos identifiants Twilio
    public static final String ACCOUNT_SID = "ACb100a3fccaa0ebb9efdd20d824812460";
    public static final String AUTH_TOKEN = "c4dc490a2e2957a9e735f397c0f16f52";
    public static final String TWILIO_PHONE_NUMBER = "+15096001049"; // Votre numéro Twilio

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSMS(String toPhoneNumber, String messageBody) {
        try {
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber),
                            new PhoneNumber(TWILIO_PHONE_NUMBER),
                            messageBody)
                    .create();

            System.out.println("Message SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
        }
    }

    public static void sendWhatsApp(String toPhoneNumber, String messageBody) {
        try {
            // Format du numéro pour WhatsApp: "whatsapp:+1234567890"
            Message message = Message.creator(
                            new PhoneNumber("whatsapp:" + toPhoneNumber),
                            new PhoneNumber("whatsapp:" + TWILIO_PHONE_NUMBER),
                            messageBody)
                    .create();

            System.out.println("Message SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du WhatsApp: " + e.getMessage());
        }
    }
}