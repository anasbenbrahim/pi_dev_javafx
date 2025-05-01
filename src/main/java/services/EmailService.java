
package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class EmailService {
    private final String username;
    private final String password;

    public EmailService() {
        Properties props = new Properties();
        try {
            props.load(EmailService.class.getClassLoader().getResourceAsStream("application.properties"));
            this.username = props.getProperty("email.username");
            this.password = props.getProperty("email.password");
            if (username == null || password == null) {
                throw new IllegalStateException("Email username or password not found in application.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email configuration", e);
        }
    }

    public void sendEmail(String toEmail, String subject, String body) {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.host", "smtp.gmail.com");
        mailProps.put("mail.smtp.port", "587");

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Error sending email to " + toEmail + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
