package com.farmer.service;

import com.farmer.entity.Labor;
import com.farmer.entity.LaborBooking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class LaborEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail; // Get from application.properties

    // ✅ Send Labor Booking Request Email
    public void sendLaborBookingRequestEmail(Labor labor, LaborBooking booking) {
        String recipientEmail = labor.getEmail();
        String subject = "👨‍🌾 New Labor Booking Request";
        String content = generateLaborBookingRequestContent(labor, booking);

        sendHtmlEmail(recipientEmail, subject, content);
    }

    // ✅ Send Labor Booking Status Update Email
    public void sendLaborBookingStatusEmail(LaborBooking booking) {
        String recipientEmail = booking.getFarmerEmail(); // Farmer who booked labor
        String subject = "👨‍🌾 Labor Booking Status Update: " + booking.getStatus();
        String content = generateLaborBookingStatusContent(booking);

        sendHtmlEmail(recipientEmail, subject, content);
    }

    // ✅ Send Payment Receipt Email for Labor Booking
    public void sendLaborPaymentReceiptEmail(LaborBooking booking) {
        String recipientEmail = booking.getFarmerEmail(); // Farmer's Email
        String subject = "✅ Payment Receipt for Labor Booking";
        String content = generateLaborPaymentReceiptContent(booking);

        sendHtmlEmail(recipientEmail, subject, content);
    }

    // ✅ Generate Labor Booking Request HTML Content
    private String generateLaborBookingRequestContent(Labor labor, LaborBooking booking) {
        return "<html><body>"
                + "<h3>👨‍🌾 New Labor Booking Request</h3>"
                + "<p>Hello " + labor.getName() + ",</p>"
                + "<p>You have a new booking request for your services.</p>"
                + "<p>Farmer ID: " + booking.getFarmerId() + "</p>"
                + "<p>Requested Dates: " + booking.getStartDate() + " to " + booking.getEndDate() + "</p>"
                + "<p>Total Amount: ₹" + calculateTotalPrice(booking) + "</p>"
                + "<p>Please log in to approve or reject the request.</p>"
                + "</body></html>";
    }

    // ✅ Generate Labor Booking Status HTML Content
    private String generateLaborBookingStatusContent(LaborBooking booking) {
        return "<html><body>"
                + "<h3>👨‍🌾 Labor Booking Status Update</h3>"
                + "<p>Hello,</p>"
                + "<p>Your booking for labor <strong>" + booking.getLabor().getName() + "</strong> has been <strong>"
                + booking.getStatus() + "</strong>.</p>"
                + "<p>Start Date: " + booking.getStartDate() + "</p>"
                + "<p>End Date: " + booking.getEndDate() + "</p>"
                + "<p>Thank you for using our platform! 🌱</p>"
                + "</body></html>";
    }

    // ✅ Generate Labor Payment Receipt HTML Content
    private String generateLaborPaymentReceiptContent(LaborBooking booking) {
        return "<html><body>"
                + "<h3>✅ Payment Receipt for Labor Booking</h3>"
                + "<p>Hello,</p>"
                + "<p>Your payment for labor <strong>" + booking.getLabor().getName() + "</strong> has been successfully completed.</p>"
                + "<p>Total Amount: ₹" + calculateTotalPrice(booking) + "</p>"
                + "<p>Payment Method: " + booking.getPaymentMethod() + "</p>"
                + "<p>Thank you for using Farmer Labor Rental Platform! 👨‍🌾</p>"
                + "</body></html>";
    }

    // ✅ Calculate Total Price for Labor Booking
    private double calculateTotalPrice(LaborBooking booking) {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
        return booking.getLabor().getPricePerDay() * daysBetween;
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
