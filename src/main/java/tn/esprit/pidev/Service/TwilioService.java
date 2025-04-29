package tn.esprit.pidev.Service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {
    // Remplacez par vos identifiants Twilio
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
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