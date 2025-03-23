package com.farmer.service;

import com.farmer.entity.Booking;
import com.farmer.entity.Farmer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private FarmerService farmerService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail; // Get from application.properties

    // ✅ Send Booking Request Email
    public void sendBookingRequestEmail(Farmer owner, Farmer borrower, Booking booking) {
        String recipientEmail = owner.getEmail();
        String subject = "🚜 New Equipment Booking Request";
        String content = generateBookingRequestContent(owner, borrower, booking);

        sendHtmlEmail(recipientEmail, subject, content);
    }

    // ✅ Send Booking Status Update Email
    public void sendBookingStatusEmail(Booking booking) {
        String recipientEmail = booking.getBorrower().getEmail();
        String subject = "🚜 Booking Status Update: " + booking.getStatus();
        String content = generateBookingStatusContent(booking);

        sendHtmlEmail(recipientEmail, subject, content);
    }

    // ✅ Send Payment Receipt Email
    public void sendPaymentReceiptEmail(Booking booking) {
        String recipientEmail = booking.getBorrower().getEmail();
        String subject = "✅ Payment Receipt for Equipment Booking";
        String content = generatePaymentReceiptContent(booking);

        sendHtmlEmail(recipientEmail, subject, content);
    }

    // ✅ Send HTML Invitation Email
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
            String htmlContent = generateInvitationHtmlContent(invitedName, inviterName, inviterEmail);
            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("✅ HTML invitation email sent successfully to: " + invitedEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
        }
    }

    // ✅ Generate Booking Request HTML Content
    private String generateBookingRequestContent(Farmer owner, Farmer borrower, Booking booking) {
        return "<html><body>"
                + "<h3>🚜 New Equipment Booking Request</h3>"
                + "<p>Hello " + owner.getFirstName() + ",</p>"
                + "<p>You have a new booking request from <strong>" + borrower.getFirstName() + " " + borrower.getLastName() + "</strong> for your equipment.</p>"
                + "<p>Equipment Name: " + booking.getEquipment().getName() + "</p>"
                + "<p>Requested Dates: " + booking.getStartDate() + " to " + booking.getEndDate() + "</p>"
                + "<p>Please log in to approve or reject the request.</p>"
                + "</body></html>";
    }

    // ✅ Generate Booking Status HTML Content
    private String generateBookingStatusContent(Booking booking) {
        return "<html><body>"
                + "<h3>🚜 Booking Status Update</h3>"
                + "<p>Hello " + booking.getBorrower().getFirstName() + ",</p>"
                + "<p>Your booking for equipment <strong>" + booking.getEquipment().getName() + "</strong> has been <strong>"
                + booking.getStatus() + "</strong>.</p>"
                + "<p>Start Date: " + booking.getStartDate() + "</p>"
                + "<p>End Date: " + booking.getEndDate() + "</p>"
                + "<p>Thank you for using our platform! 🌱</p>"
                + "</body></html>";
    }

    // ✅ Generate Payment Receipt HTML Content
    private String generatePaymentReceiptContent(Booking booking) {
        return "<html><body>"
                + "<h3>✅ Payment Receipt for Equipment Booking</h3>"
                + "<p>Hello " + booking.getBorrower().getFirstName() + ",</p>"
                + "<p>Your payment for equipment <strong>" + booking.getEquipment().getName() + "</strong> has been successfully completed.</p>"
                + "<p>Total Amount: ₹" + calculateTotalPrice(booking) + "</p>"
                + "<p>Payment Method: " + booking.getPaymentMethod() + "</p>"
                + "<p>Thank you for using Farmer Equipment Rental Platform! 🚜</p>"
                + "</body></html>";
    }

    // ✅ Generate Invitation HTML Content
    private String generateInvitationHtmlContent(String invitedName, String inviterName, String inviterEmail) {
        return "<!DOCTYPE html>"
                + "<html><head><style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; text-align: center; }"
                + ".email-container { background: white; padding: 20px; border-radius: 10px; max-width: 500px; margin: auto; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2); }"
                + ".header { color: #28a745; font-size: 20px; font-weight: bold; }"
                + ".content { margin-top: 10px; font-size: 16px; color: #333; }"
                + ".cta-button { display: inline-block; margin-top: 15px; padding: 12px 20px; background-color: #ffc107; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }"
                + ".footer { margin-top: 20px; font-size: 14px; color: gray; }"
                + "</style></head>"
                + "<body>"
                + "<div class='email-container'>"
                + "<p class='header'>🚜 Join the Farmer Equipment Rental Platform!</p>"
                + "<p class='content'>Hello <strong>" + invitedName + "</strong>,</p>"
                + "<p class='content'><strong>" + inviterName + "</strong> (<a href='mailto:" + inviterEmail + "'>" + inviterEmail + "</a>)"
                + " has invited you to explore the <b>Farmer Equipment Rental Platform</b>!</p>"
                + "<p class='content'>You can now rent, share, and manage farming equipment easily. Click the button below to get started.</p>"
                + "<a href='https://farmerental.example.com' class='cta-button'>Explore Rentals 🚜</a>"
                + "<p class='footer'>Empowering farmers with technology! 🌱</p>"
                + "</div></body></html>";
    }

    // ✅ Calculate Total Price
    private double calculateTotalPrice(Booking booking) {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
        return booking.getEquipment().getPricePerDay() * daysBetween;
    }

    // ✅ Send HTML Email
    private void sendHtmlEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setText(content, true);

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
        }
    }
}