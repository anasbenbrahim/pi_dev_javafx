package tn.esprit.pidev;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class EmailService {
    // Store verification codes with email as key
    private static final Map<String, String> verificationCodes = new HashMap<>();
    // Code expiration time in milliseconds (10 minutes)
    private static final long CODE_EXPIRATION_TIME = 10 * 60 * 1000;
    // Store code generation timestamps
    private static final Map<String, Long> codeTimestamps = new HashMap<>();

    // Email configuration - replace with your SMTP details
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "anasbenbrahim491@gmail.com"; // Replace with actual email
    private static final String EMAIL_PASSWORD = "vkgw tqui loio ciro"; // Replace with actual password

    /**
     * Generates a random 6-digit verification code
     * @return The generated code
     */
    private static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }

    /**
     * Sends a verification code to the specified email
     * @param email The recipient's email
     * @return The generated verification code
     */
    public static String sendVerificationCode(String email) {
        String code = generateVerificationCode();

        // Store the code and timestamp
        verificationCodes.put(email, code);
        codeTimestamps.put(email, System.currentTimeMillis());

        // Send the email
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Account Verification Code");
            message.setText("Your verification code is: " + code + "\n\nThis code will expire in 10 minutes.");

            Transport.send(message);
            System.out.println("Verification code sent to: " + email);
            return code;
        } catch (MessagingException e) {
            System.out.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifies if the provided code matches the stored code for the email
     * @param email The email address
     * @param code The code to verify
     * @return True if the code is valid, false otherwise
     */
    public static boolean verifyCode(String email, String code) {
        // Check if the code exists and matches
        if (!verificationCodes.containsKey(email)) {
            return false;
        }

        // Check if the code has expired
        long timestamp = codeTimestamps.get(email);
        if (System.currentTimeMillis() - timestamp > CODE_EXPIRATION_TIME) {
            // Remove expired code
            verificationCodes.remove(email);
            codeTimestamps.remove(email);
            return false;
        }

        // Verify the code
        boolean isValid = verificationCodes.get(email).equals(code);

        // If valid, remove the code (one-time use)
        if (isValid) {
            verificationCodes.remove(email);
            codeTimestamps.remove(email);
        }

        return isValid;
    }



}