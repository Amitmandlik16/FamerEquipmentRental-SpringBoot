package com.farmer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.farmer.entity.Farmer;

@Service
public class EmailService {

	@Autowired
	private FarmerService farmerService;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail; // Get from application.properties

	// Method to send HTML invitation email for equipment rental
	public void sendHtmlInvitationEmail(String invitedName, String invitedEmail, long id) {
		try {
			Farmer farmer = farmerService.getFarmerById(id);
			String inviterName = farmer.getFirstName() + " " + farmer.getLastName();
			String inviterEmail = farmer.getEmail();

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(invitedEmail);
			helper.setSubject("🚜 " + inviterName + " Invited You to Rent Farming Equipment!");
			helper.setFrom(fromEmail);

			// Generate HTML email content
			String htmlContent = generateHtmlContent(invitedName, inviterName, inviterEmail);

			helper.setText(htmlContent, true);
			mailSender.send(message);

			System.out.println("✅ HTML invitation email sent successfully to: " + invitedEmail);
		} catch (MessagingException e) {
			System.err.println("❌ Error sending email: " + e.getMessage());
		}
	}

	// Generate HTML content for equipment rental
	private String generateHtmlContent(String invitedName, String inviterName, String inviterEmail) {
		return "<!DOCTYPE html>" + "<html><head><style>"
				+ "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; text-align: center; }"
				+ ".email-container { background: white; padding: 20px; border-radius: 10px; max-width: 500px; margin: auto; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2); }"
				+ ".header { color: #28a745; font-size: 20px; font-weight: bold; }"
				+ ".content { margin-top: 10px; font-size: 16px; color: #333; }"
				+ ".cta-button { display: inline-block; margin-top: 15px; padding: 12px 20px; background-color: #ffc107; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }"
				+ ".footer { margin-top: 20px; font-size: 14px; color: gray; }" + "</style></head>" + "<body>"
				+ "<div class='email-container'>"
				+ "<p class='header'>🚜 Join the Farmer Equipment Rental Platform!</p>"
				+ "<p class='content'>Hello <strong>" + invitedName + "</strong>,</p>" + "<p class='content'><strong>"
				+ inviterName + "</strong> (<a href='mailto:" + inviterEmail + "'>" + inviterEmail + "</a>)"
				+ " has invited you to explore the <b>Farmer Equipment Rental Platform</b>!</p>"
				+ "<p class='content'>You can now rent, share, and manage farming equipment easily. Click the button below to get started.</p>"
				+ "<a href='https://farmerental.example.com' class='cta-button'>Explore Rentals 🚜</a>"
				+ "<p class='footer'>Empowering farmers with technology! 🌱</p>" + "</div></body></html>";
	}
}
