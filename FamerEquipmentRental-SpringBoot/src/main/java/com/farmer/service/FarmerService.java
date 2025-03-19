package com.farmer.service;

import com.farmer.dto.ForgotPasswordRequestDTO;
import com.farmer.entity.Farmer;
import com.farmer.repository.FarmerRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class FarmerService {

	public static final String ACCOUNT_SID = "ACdffbe44d72d4e6eef69fb01203b7447a";
	public static final String AUTH_TOKEN = "699174fd0cc518640a16cee6e457634c";

	@Autowired
	private FarmerRepository farmerRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private FileService fileService;

	public boolean isUsernameExists(String username) {
		return farmerRepo.existsByUsername(username);
	}

	public boolean isMobileNumberExists(String mobileNumber) {
		return farmerRepo.existsByMobileNumber(mobileNumber);
	}

	public boolean isEmailExists(String email) {
		return farmerRepo.existsByEmail(email);
	}

	// ✅ Login logic
	public Farmer login(String username, String password) {
		return farmerRepo.findByUsername(username).filter(farmer -> farmer.getPassword().equals(password)).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
	}

	// ✅ Register new farmer
	public Farmer createFarmer(Farmer farmer) throws IOException {
		farmer.setTotalEquipment(0);
		farmer.setTotalRentalsGiven(0);
		farmer.setTotalRentalsTaken(0);
		farmer.setTotalRewards(0);
		farmer.setRatingAsGiver(0);
		farmer.setRatingAsTaker(0);
		return farmerRepo.save(farmer);
	}

	// ✅ Fetch farmer by ID
	public Farmer getFarmerById(long id) {
		return farmerRepo.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found with ID: " + id));
	}

	// ✅ Update farmer details
	public Farmer updateFarmer(long id, Farmer farmerDetails) {
		Farmer farmer = getFarmerById(id);

		farmer.setUsername(farmerDetails.getUsername());
		farmer.setFirstName(farmerDetails.getFirstName());
		farmer.setMiddleName(farmerDetails.getMiddleName());
		farmer.setLastName(farmerDetails.getLastName());
		farmer.setProfileImg(farmerDetails.getProfileImg());
		farmer.setCountry(farmerDetails.getCountry());
		farmer.setState(farmerDetails.getState());
		farmer.setTaluka(farmerDetails.getTaluka());
		farmer.setDistrict(farmerDetails.getDistrict());
		farmer.setVillage(farmerDetails.getVillage());
		farmer.setPincode(farmerDetails.getPincode());
		farmer.setLongitude(farmerDetails.getLongitude());
		farmer.setLatitude(farmerDetails.getLatitude());
		farmer.setDOB(farmerDetails.getDOB());
		farmer.setLandmark(farmerDetails.getLandmark());
		farmer.setMobileNumber(farmerDetails.getMobileNumber());
		farmer.setEmail(farmerDetails.getEmail());

		return farmerRepo.save(farmer);
	}

	// ✅ Update password logic
	public String updatePassword(String username, String oldPassword, String newPassword) {
		Farmer farmer = farmerRepo.findByUsername(username).filter(f -> f.getPassword().equals(oldPassword))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Invalid credentials or user not found"));

		farmer.setPassword(newPassword);
		farmerRepo.save(farmer);
		return "Password updated successfully";
	}

// ✅ Delete farmer logic
	public Map<String, Boolean> deleteFarmer(Long id) {
		// Fetch the Farmer by ID
		Farmer farmer = getFarmerById(id);

		farmerRepo.delete(farmer);
		return null;
	}

	// ✅ Forgot password logic
	public String forgotPassword(ForgotPasswordRequestDTO request) {
		Farmer farmer = farmerRepo.findByUsernameAndEmail(request.getUsername(), request.getEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

		// Generate a temporary password
		String tempPassword = UUID.randomUUID().toString().substring(0, 8);

		// Update the password in the database
		farmer.setPassword(tempPassword);
		farmerRepo.save(farmer);

		// Send email with new password
		sendEmail(farmer.getEmail(), farmer.getUsername(), farmer.getFirstName(), farmer.getLastName(), tempPassword);
		sendSms(farmer.getMobileNumber(), farmer.getUsername(), farmer.getFirstName(), farmer.getLastName(),
				tempPassword);

		return "A temporary password has been sent to your email.";
	}

	private void sendEmail(String toEmail, String userName, String firstName, String lastName, String tempPassword) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(toEmail);
			helper.setSubject("🚜 Farmer Equipment Rental - Password Reset Request");

			// ✅ Farmer Equipment Rental Logo URL (Replace with actual hosted image)
			String logoUrl = "https://clipart-library.com/images/6cR5X6yqi.png"; // 🔄 Update with actual image URL

			// ✅ HTML Email Content
			String emailContent = "<!DOCTYPE html>" + "<html><head><style>"
					+ "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
					+ ".container { max-width: 500px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1); text-align: center; }"
					+ ".logo { width: 100px; margin-bottom: 15px; }"
					+ ".header { font-size: 22px; font-weight: bold; color: #4CAF50; }"
					+ ".content { font-size: 16px; color: #333; margin: 15px 0; }"
					+ ".password { font-size: 22px; font-weight: bold; color: #4CAF50; background: #f0f0f0; padding: 10px; display: inline-block; border-radius: 5px; }"
					+ ".button { background-color: #4CAF50; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 10px; font-size: 16px; }"
					+ ".footer { font-size: 14px; color: #777; margin-top: 20px; }" + "</style></head><body>"
					+ "<div class='container'>" + "<img src='" + logoUrl + "' class='logo' alt='Farmer Rental Logo' />"
					+ "<div class='header'>🚜 Password Reset Request</div>" + "<div class='content'>Hello <b>"
					+ firstName + " " + lastName + "</b><br> (<b>" + "UserName: " + userName + "</b>),</div>"
					+ "<div class='content'>We received a request to reset your password for your Farmer Equipment Rental account.</div>"
					+ "<div class='content'>Your new temporary password is:</div>" + "<div class='password'>"
					+ tempPassword + "</div>"
					+ "<div class='content'>Please log in using this temporary password and change it immediately for security.</div>"
					+ "<div class='content'><a href='http://localhost:4200/login' class='button'>Login to Farmer Rental</a></div>"
					+ "<div class='footer'>If you didn’t request this, please contact support.</div>"
					+ "<div class='footer'>🌾 Thank you for using Farmer Equipment Rental Service!</div>"
					+ "</div></body></html>";

			helper.setText(emailContent, true); // Enable HTML format
			mailSender.send(message);

		} catch (MessagingException e) {
			System.out.println("Error sending email: " + e.getMessage());
		}
	}

	public static void sendSms(String toPhoneNumber, String userName, String firstName, String lastName,
			String tempPassword) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		Message message = Message.creator(new PhoneNumber("+91" + toPhoneNumber), // Recipient
				new PhoneNumber("+18483445411"), // Twilio Phone Number
				"\n\n🚜 Farmer Equipment Rental: \nHello " + firstName + " " + lastName + "\nUserName: " + userName
						+ "\nYour temporary password is " + tempPassword + ". Please update it after logging in.")
				.create();

		System.out.println("SMS sent! SID: " + message.getSid());
	}

}
