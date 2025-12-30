package com.gemsflare.gemsflare.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "Welcome to Gemsflare!";

        String body = "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }"
                + ".header { text-align: center; padding: 20px 0; }"
                + ".header img { width: 120px; }"
                + ".content { text-align: center; font-size: 18px; margin-top: 20px; }"
                + ".button { display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin-top: 20px; }"
                + ".footer { text-align: center; font-size: 14px; margin-top: 30px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<img src='https://cdn1.iconfinder.com/data/icons/medieval-7/128/medieval_gem_emerald_diamond_crystal_stone_treasure-1024.png' alt='Gemsflare Logo' />"
                + "</div>"
                + "<div class='content'>"
                + "<h1>Welcome, " + username + "!</h1>"
                + "<p>Thank you for signing up for Gemsflare. We're excited to have you with us!</p>"
                + "<p>If you have any questions, feel free to reach out to us at any time.</p>"
                + "<a href='https://gemsflare.com' class='button'>Get Started</a>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>Best regards,</p>"
                + "<p><strong>Gemsflare Team</strong></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendEmail(toEmail, subject, body);
    }

    public void sendDeletedUserEmail(String toEmail, String username) {
        String subject = "Your Gemsflare Account Has Been Deleted";

        String body = "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }"
                + ".header { text-align: center; padding: 20px 0; }"
                + ".header img { width: 120px; }"
                + ".content { text-align: center; font-size: 18px; margin-top: 20px; }"
                + ".footer { text-align: center; font-size: 14px; margin-top: 30px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<img src='https://cdn1.iconfinder.com/data/icons/medieval-7/128/medieval_gem_emerald_diamond_crystal_stone_treasure-1024.png' alt='Gemsflare Logo' />"
                + "</div>"
                + "<div class='content'>"
                + "<h1>Dear " + username + ",</h1>"
                + "<p>We regret to inform you that your account has been successfully deleted from Gemsflare.</p>"
                + "<p>If this was done by mistake or if you have any questions, please feel free to contact us.</p>"
                + "<p>We're sorry to see you go, and we hope to serve you again in the future.</p>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>Best regards,</p>"
                + "<p><strong>Gemsflare Team</strong></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendEmail(toEmail, subject, body);
    }

    public void sendEditedProfileEmail(String toEmail, String username) {
        String subject = "Your Gemsflare Profile Has Been Updated";

        String body = "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }"
                + ".header { text-align: center; padding: 20px 0; }"
                + ".header img { width: 120px; }"
                + ".content { text-align: center; font-size: 18px; margin-top: 20px; }"
                + ".footer { text-align: center; font-size: 14px; margin-top: 30px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<img src='https://cdn1.iconfinder.com/data/icons/medieval-7/128/medieval_gem_emerald_diamond_crystal_stone_treasure-1024.png' alt='Gemsflare Logo' />"
                + "</div>"
                + "<div class='content'>"
                + "<h1>Dear " + username + ",</h1>"
                + "<p>Your Gemsflare profile has been successfully updated.</p>"
                + "<p>If you did not make this change or have any questions, feel free to contact us.</p>"
                + "<p>We are always here to assist you!</p>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>Best regards,</p>"
                + "<p><strong>Gemsflare Team</strong></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendEmail(toEmail, subject, body);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true enables HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}