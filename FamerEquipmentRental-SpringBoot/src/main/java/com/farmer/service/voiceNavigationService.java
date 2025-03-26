package com.farmer.service;

import com.farmer.entity.Booking;
import com.farmer.entity.Farmer;
import com.farmer.entity.Labor;
import com.farmer.entity.LaborBooking;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//voice Controlled Navigation System

@Service
public class voiceNavigationService {

	@Autowired
	DeepgramService deepgramService;

	@Autowired
	EquipmentService equipmentService;

	@Autowired
	ComplaintService complaintService;

	@Autowired
	EmailService emailService;

	@Autowired
	BookingService bookingService;

	@Autowired
	FarmerService farmerService;

	@Autowired
	LaborService laborService;

	@Autowired
	LaborBookingService laborBookingService;
	// Define expected commands and actions
	private static final Map<String, String> commandResponses = new HashMap<>();

	static {
		commandResponses.put("book equipment", "Booking equipment. Please specify the dates.");
		commandResponses.put("cancel booking", "Booking has been cancelled.");
		commandResponses.put("check availability", "Checking availability. Please wait.");
		commandResponses.put("get equipment", "Fetching equipment details.");
		commandResponses.put("register complaint", "Registering a complaint. Please provide the details.");
		commandResponses.put("exit", "Exiting the system. Goodbye!");
		commandResponses.put("send invitation email", "Sending invitation email. Please provide recipient details.");
		commandResponses.put("submit feedback", "Submitting feedback. Please provide your rating and comments.");
		commandResponses.put("approve booking", "Approving booking. Please specify booking ID and status.");
		commandResponses.put("reject booking", "Rejecting booking. Please specify booking ID and status.");
		commandResponses.put("mark received", "Marking equipment as received. Please specify the booking ID.");
		commandResponses.put("check booking calendar", "Checking booking calendar. Please specify equipment ID.");
		commandResponses.put("get labors by skill", "Fetching labors by skill. Please specify the skill.");
		commandResponses.put("get labors by location", "Fetching labors by location. Please specify the location.");
		commandResponses.put("book labor", "Booking labor. Please specify the labor ID and dates.");
		commandResponses.put("approve labor booking", "Approving labor booking. Please specify the booking ID.");
		commandResponses.put("reject labor booking", "Rejecting labor booking. Please specify the booking ID.");
		commandResponses.put("complete labor booking",
				"Marking labor booking as completed. Please specify the booking ID.");
		commandResponses.put("check labor availability", "Checking labor availability. Please specify the labor ID.");
		commandResponses.put("get booking requests", "Fetching all booking requests for the farmer.");

		// ✅ Add Basic Conversation and Greetings Commands
		commandResponses.put("hi", "Hello! How can I assist you today? 😊");
		commandResponses.put("hello", "Hi! What can I do for you? 🚜");
		commandResponses.put("hey", "Hey! Need help with anything? 🌱");
		commandResponses.put("good morning", "Good morning! Ready to manage your farm? 🌞");
		commandResponses.put("good evening", "Good evening! Hope you're having a great day! 🌇");
		commandResponses.put("good afternoon", "Good afternoon! How can I assist you? 🌾");

		commandResponses.put("goodbye", "Goodbye! Have a productive day! 🌱");
		commandResponses.put("bye", "Bye! See you soon! 👋");
		commandResponses.put("see you later", "See you later! Take care! 😊");
		commandResponses.put("exit", "Exiting the system. Goodbye! 🛑");
		commandResponses.put("close the application", "Closing the application. Have a great day! 🌾");

		commandResponses.put("how are you", "I'm doing great! How can I assist you today? 😊");
		commandResponses.put("what's up", "All good here! Ready to manage your equipment? 🚜");
		commandResponses.put("how’s it going", "I'm functioning perfectly! What task can I help you with? 🤖");

		commandResponses.put("i need help",
				"Of course! I can help you with booking equipment, managing labor, checking availability, and more. 📚");
		commandResponses.put("can you assist me",
				"Sure! I can assist you with booking, complaints, checking availability, and more. 🚜");
		commandResponses.put("what can you do",
				"I can help you with booking, complaints, checking availability, and more. Say 'help' anytime for available commands. 🤖");
		commandResponses.put("show me available options",
				"Here’s a list of commands I can handle. Say 'help' anytime to see them again! 📚");

		commandResponses.put("thank you", "You're welcome! Always happy to help! 😊");
		commandResponses.put("thanks a lot", "My pleasure! Let me know if you need anything else! 🚜");
		commandResponses.put("i appreciate your help", "Glad I could assist! Keep growing strong! 🌱");

		commandResponses.put("tell me a joke",
				"Why did the scarecrow win an award? Because he was outstanding in his field! 😂");
		commandResponses.put("do you like farming",
				"I love helping farmers! Agriculture is the heart of the world. 🌾");
		commandResponses.put("are you smart", "I'm smart enough to make farming easier for you! 🤖");
		commandResponses.put("what's your name", "My name is AgriBot, your smart farming assistant! 🚜");

		commandResponses.put("sorry", "No worries! Let’s fix it together. 😊");
		commandResponses.put("my bad", "It's okay! What can I do for you now? 🤗");
		commandResponses.put("oops, I made a mistake", "Mistakes happen! Let’s move forward! 🚜");

		commandResponses.put("what's the time", "The current time is [current_time]. ⏰");
		commandResponses.put("what's today's date", "Today's date is [current_date]. 📅");

		commandResponses.put("what's the weather",
				"Weather updates are coming soon! Stay tuned for the next version! 🌦️");
		commandResponses.put("will it rain tomorrow",
				"I'm learning to provide weather updates. This feature will be available shortly! 🌱");

	}

	public ResponseEntity<ByteArrayResource> voiceReadProcess(File file, Long farmerId) {
		String readText = deepgramService.transcribeAudio(file);
		System.out.println("Farmer Id:" + farmerId + "->Read Text: " + readText);

		// Check for a matching command
		String speakText = processCommand(readText.toLowerCase(), farmerId);
		System.out.println("Farmer Id:" + farmerId + "->Speak Text: " + speakText);

		// Convert response text to audio
		ResponseEntity<ByteArrayResource> audioResource = deepgramService.textToSpeech(speakText);
		return audioResource;
	}

	private String processCommand(String readText, Long farmerId) {
		if (readText.contains("what's the time")) {
			return "The current time is " + getCurrentTime() + " ⏰";
		} else if (readText.contains("what's today's date")) {
			return "Today's date is " + getCurrentDate() + " 📅";
		}

		JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
		String bestMatch = null;
		double bestScore = 0.0;

		for (String command : commandResponses.keySet()) {
			double similarity = jaroWinkler.apply(readText, command);
			if (similarity > bestScore) {
				bestScore = similarity;
				bestMatch = command;
			}
		}

		// ✅ Handle Unknown Commands or Unclear User Input
		if (bestMatch == null) {
			return "I'm not sure how to respond to that. Try using one of my available commands, or say 'help' for assistance. 🤔";
		}
		if (bestMatch == null || bestScore < 0.5) {
			return "Sorry, I didn't catch that. You can say 'help' for a list of available commands. 🤔";
		}
		

		// If similarity is 50% or higher, process the command
		if (bestScore >= 0.50) {
			// Extract relevant data if matched
			Map<String, String> extractedData = extractData(readText);

			if (!extractedData.isEmpty()) {
				return performAction(bestMatch, extractedData, farmerId);
			} else {
				// ✅ Return best-matched command response
				return commandResponses.get(bestMatch);
				// Command matched but no data extracted
			}
		} else {
			return "Sorry, I didn't understand that. Try using standard commands.";
		}
	}

	// ✅ Get Current Time
	private String getCurrentTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
		return LocalDateTime.now().format(formatter);
	}

	// ✅ Get Current Date
	private String getCurrentDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDateTime.now().format(formatter);
	}

	// ✅ Extract relevant data (like equipment ID or complaint details)
	private Map<String, String> extractData(String readText) {
		Map<String, String> extractedData = new HashMap<>();

		// Pattern to extract farmer ID
		Pattern farmerPattern = Pattern.compile(
				".*?\\b(?:farmer|id)\\s*(\\d+|one|two|three|four|five|six|seven|eight|nine|ten).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher farmerMatcher = farmerPattern.matcher(readText);

		if (farmerMatcher.find()) {
			extractedData.put("farmerId", convertWordToNumber(farmerMatcher.group(1)));
		}

		// Pattern to extract booking ID for labor
		Pattern laborBookingPattern = Pattern.compile(
				".*?\\b(?:booking|labor|id)\\s*(\\d+|one|two|three|four|five|six|seven|eight|nine|ten).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher laborBookingMatcher = laborBookingPattern.matcher(readText);

		if (laborBookingMatcher.find()) {
			extractedData.put("bookingId", convertWordToNumber(laborBookingMatcher.group(1)));
		}

		// Pattern to extract status for approval/rejection
		Pattern statusPattern = Pattern.compile(".*?\\b(?:status|approve|reject)\\s*(\\w+).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher statusMatcher = statusPattern.matcher(readText);

		if (statusMatcher.find()) {
			extractedData.put("status", statusMatcher.group(1).toUpperCase());
		}

		// Pattern to extract labor ID
		Pattern laborPattern = Pattern.compile(
				".*?\\b(?:labor|id|worker)\\s*(\\d+|one|two|three|four|five|six|seven|eight|nine|ten).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher laborMatcher = laborPattern.matcher(readText);

		if (laborMatcher.find()) {
			extractedData.put("laborId", convertWordToNumber(laborMatcher.group(1)));
		}

		// Pattern to extract start date
		Pattern startDatePattern = Pattern.compile(".*?start date (\\d{4}-\\d{2}-\\d{2}).*?", Pattern.CASE_INSENSITIVE);
		Matcher startDateMatcher = startDatePattern.matcher(readText);

		if (startDateMatcher.find()) {
			extractedData.put("startDate", startDateMatcher.group(1));
		}

		// Pattern to extract end date
		Pattern endDatePattern = Pattern.compile(".*?end date (\\d{4}-\\d{2}-\\d{2}).*?", Pattern.CASE_INSENSITIVE);
		Matcher endDateMatcher = endDatePattern.matcher(readText);

		if (endDateMatcher.find()) {
			extractedData.put("endDate", endDateMatcher.group(1));
		}

		// Pattern to extract payment method
		Pattern paymentPattern = Pattern.compile(".*?\\b(?:payment method|pay)\\s*(\\w+).*?", Pattern.CASE_INSENSITIVE);
		Matcher paymentMatcher = paymentPattern.matcher(readText);

		if (paymentMatcher.find()) {
			extractedData.put("paymentMethod", paymentMatcher.group(1).trim().toUpperCase());
		}

		// Pattern to extract skill for labor search
		Pattern skillPattern = Pattern.compile(".*?(skill|specialty|expertise)\\s*(\\w+).*?", Pattern.CASE_INSENSITIVE);
		Matcher skillMatcher = skillPattern.matcher(readText);

		if (skillMatcher.find()) {
			extractedData.put("skill", skillMatcher.group(2).trim());
		}

		// Pattern to extract location for labor search
		Pattern locationPattern = Pattern.compile(".*?(location|area|place)\\s*(\\w+).*?", Pattern.CASE_INSENSITIVE);
		Matcher locationMatcher = locationPattern.matcher(readText);

		if (locationMatcher.find()) {
			extractedData.put("location", locationMatcher.group(2).trim());
		}

		// Pattern to extract equipment ID for booking calendar
		Pattern calendarPattern = Pattern.compile(".*?check.*?(availability|calendar).*?equipment\\s*(\\d+).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher calendarMatcher = calendarPattern.matcher(readText);

		if (calendarMatcher.find()) {
			extractedData.put("equipmentId", calendarMatcher.group(2));
		}

		// Pattern to extract booking ID and status (approve/reject)
		Pattern bookingStatusPattern = Pattern
				.compile(".*?(approve|reject)\\s*booking\\s*(\\d+).*?(approved|rejected).*?", Pattern.CASE_INSENSITIVE);
		Matcher bookingStatusMatcher = bookingStatusPattern.matcher(readText);

		if (bookingStatusMatcher.find()) {
			extractedData.put("bookingId", bookingStatusMatcher.group(2)); // Extract booking ID
			extractedData.put("status", bookingStatusMatcher.group(3).toUpperCase()); // Extract status
		}

		// Pattern to extract booking ID for marking as received
		Pattern markReceivedPattern = Pattern.compile(".*?mark.*?received.*?booking\\s*(\\d+).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher markReceivedMatcher = markReceivedPattern.matcher(readText);

		if (markReceivedMatcher.find()) {
			extractedData.put("bookingId", markReceivedMatcher.group(1));
		}

		// ✅ Extract Booking ID for Cancellation or Marking as Received
		Pattern bookingPattern = Pattern.compile(".*?\\b(?:booking|id|number)\\s*(\\d+).*?", Pattern.CASE_INSENSITIVE);
		Matcher bookingMatcher = bookingPattern.matcher(readText);

		if (bookingMatcher.find()) {
			extractedData.put("bookingId", bookingMatcher.group(1));
		}

		// ✅ Extract startDate and endDate
		Pattern datePattern = Pattern.compile(".*?(\\d{4}-\\d{2}-\\d{2})\\s*(?:to|until)\\s*(\\d{4}-\\d{2}-\\d{2}).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher dateMatcher = datePattern.matcher(readText);

		if (dateMatcher.find()) {
			extractedData.put("startDate", dateMatcher.group(1));
			extractedData.put("endDate", dateMatcher.group(2));
		}

		Pattern ratingPattern = Pattern.compile(".*?\\b(?:rating|score)\\s*(\\d).*?", Pattern.CASE_INSENSITIVE);
		Matcher ratingMatcher = ratingPattern.matcher(readText);

		if (ratingMatcher.find()) {
			extractedData.put("rating", ratingMatcher.group(1).trim());
		}

		Pattern improvementPattern = Pattern.compile(".*?\\b(?:improvements|suggestions)\\s*?(.*?)(?:\\.|,|$)",
				Pattern.CASE_INSENSITIVE);
		Matcher improvementMatcher = improvementPattern.matcher(readText);

		if (improvementMatcher.find()) {
			extractedData.put("improvements", improvementMatcher.group(1).trim());
		}

		Pattern issuesPattern = Pattern.compile(".*?\\b(?:issues|problems)\\s*?(.*?)(?:\\.|,|$)",
				Pattern.CASE_INSENSITIVE);
		Matcher issuesMatcher = issuesPattern.matcher(readText);

		if (issuesMatcher.find()) {
			extractedData.put("issues", issuesMatcher.group(1).trim());
		}

		Pattern commentsPattern = Pattern.compile(".*?\\b(?:comments|notes)\\s*?(.*?)(?:\\.|,|$)",
				Pattern.CASE_INSENSITIVE);
		Matcher commentsMatcher = commentsPattern.matcher(readText);

		if (commentsMatcher.find()) {
			extractedData.put("additionalComments", commentsMatcher.group(1).trim());
		}

		Pattern recommendPattern = Pattern.compile(".*?\\b(?:recommend|suggest)\\s*(yes|no).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher recommendMatcher = recommendPattern.matcher(readText);

		if (recommendMatcher.find()) {
			extractedData.put("recommendation", recommendMatcher.group(1).equalsIgnoreCase("yes") ? "true" : "false");
		}

		// Pattern to extract equipment ID (number or word format)
		Pattern equipmentPattern = Pattern.compile(
				".*?\\b(?:equipment|id|number)\\s*(\\d+|one|two|three|four|five|six|seven|eight|nine|ten).*?",
				Pattern.CASE_INSENSITIVE);
		Matcher equipmentMatcher = equipmentPattern.matcher(readText);

		if (equipmentMatcher.find()) {
			extractedData.put("equipmentId", convertWordToNumber(equipmentMatcher.group(1)));
		}

		// Pattern to extract complaint subject and description
		Pattern complaintPattern = Pattern.compile(".*?\\b(?:complaint|issue|problem|subject is)\\s*?(.*?)(?:\\.|,|$)",
				Pattern.CASE_INSENSITIVE);
		Matcher complaintMatcher = complaintPattern.matcher(readText);

		if (complaintMatcher.find()) {
			extractedData.put("subject", complaintMatcher.group(1).trim());
			extractedData.put("description", "Details: " + readText);
		}

		Pattern namePattern = Pattern.compile(".*?\\b(?:name|invited name)\\s*(.*?)(?:\\.|,|$)",
				Pattern.CASE_INSENSITIVE);
		Matcher nameMatcher = namePattern.matcher(readText);

		if (nameMatcher.find()) {
			extractedData.put("invitedName", nameMatcher.group(1).trim());
		}

		Pattern emailPattern = Pattern.compile(".*?\\b(?:email|invited email)\\s*(.*?)(?:\\.|,|$)",
				Pattern.CASE_INSENSITIVE);
		Matcher emailMatcher = emailPattern.matcher(readText);

		if (emailMatcher.find()) {
			extractedData.put("invitedEmail", emailMatcher.group(1).trim());
		}

		return extractedData;
	}

	// ✅ Perform action based on matched command and extracted data
	private String performAction(String command, Map<String, String> extractedData, Long farmerId) {
		if (command == null) {
			return "I'm not sure how to respond to that. Try saying 'help' for a list of available commands. 🤔";
		}
		switch (command) {
		case "tell me a joke":
			return "Why did the scarecrow win an award? Because he was outstanding in his field! 😂";
		case "do you like farming":
			return "I love helping farmers! Agriculture is the heart of the world. 🌾";
		case "are you smart":
			return "I'm smart enough to make farming easier for you! 🤖";
		case "what's your name":
			return "My name is AgriBot, your smart farming assistant! 🚜";

		case "get booking requests":
			if (extractedData.containsKey("farmerId")) {
				farmerId = Long.parseLong(extractedData.get("farmerId"));

				try {
					List<Booking> bookings = bookingService.getBookingsByFarmerId(farmerId);
					if (bookings.isEmpty()) {
						return "No booking requests found for farmer ID " + farmerId + ".";
					} else {
						return "Found " + bookings.size() + " booking requests for farmer ID " + farmerId + ".";
					}
				} catch (Exception e) {
					return "Error while fetching booking requests: " + e.getMessage();
				}
			} else {
				return "Please specify the farmer ID to get the booking requests.";
			}

		case "approve labor booking":
		case "reject labor booking":
			if (extractedData.containsKey("bookingId") && extractedData.containsKey("status")) {
				Long bookingId = Long.parseLong(extractedData.get("bookingId"));
				String status = extractedData.get("status");

				try {
					LaborBooking booking = laborBookingService.updateLaborBookingStatus(bookingId, status);
					return "Labor booking ID " + bookingId + " has been " + status.toLowerCase() + " successfully.";
				} catch (Exception e) {
					return "Error while updating labor booking status: " + e.getMessage();
				}
			} else {
				return "Please specify the booking ID and status to update the labor booking.";
			}

		case "complete labor booking":
			if (extractedData.containsKey("bookingId")) {
				Long bookingId = Long.parseLong(extractedData.get("bookingId"));

				try {
					laborBookingService.markLaborBookingAsCompleted(bookingId);
					return "Labor service marked as completed for booking ID " + bookingId + ".";
				} catch (Exception e) {
					return "Error while marking labor booking as completed: " + e.getMessage();
				}
			} else {
				return "Please specify the booking ID to mark the labor booking as completed.";
			}

		case "check labor availability":
			if (extractedData.containsKey("laborId")) {
				Long laborId = Long.parseLong(extractedData.get("laborId"));

				try {
					Map<String, Object> bookingDates = laborBookingService.getLaborBookingCalendar(laborId);
					if (bookingDates.isEmpty()) {
						return "Labor ID " + laborId + " is fully available for the requested period.";
					} else {
						return "Labor ID " + laborId + " has some booked dates. Please check the calendar.";
					}
				} catch (Exception e) {
					return "Error while checking labor availability: " + e.getMessage();
				}
			} else {
				return "Please specify the labor ID to check the availability.";
			}

		case "book labor":
			if (extractedData.containsKey("laborId") && extractedData.containsKey("startDate")
					&& extractedData.containsKey("endDate") && extractedData.containsKey("paymentMethod")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				Long laborId = Long.parseLong(extractedData.get("laborId"));
				String startDate = extractedData.get("startDate");
				LocalDate startDate2 = LocalDate.parse(startDate, formatter);
				String endDate = extractedData.get("endDate");
				LocalDate endDate2 = LocalDate.parse(endDate, formatter);
				String paymentMethod = extractedData.get("paymentMethod");

				LaborBooking laborBooking = new LaborBooking();

				laborBooking.setId(laborId);
				laborBooking.setStartDate(startDate2);
				laborBooking.setEndDate(endDate2);
				laborBooking.setPaymentMethod(paymentMethod);

				try {
					LaborBooking booking = laborBookingService.createLaborBooking(laborBooking);

					return "Labor booking request created successfully for labor ID " + laborId + " from " + startDate
							+ " to " + endDate + " with payment method " + paymentMethod + ".";
				} catch (Exception e) {
					return "Error while creating labor booking: " + e.getMessage();
				}
			} else {
				return "Please provide labor ID, start date, end date, and payment method to book the labor.";
			}

		case "get labors by skill":
			if (extractedData.containsKey("skill")) {
				String skill = extractedData.get("skill");
				try {
					List<Labor> laborsBySkill = laborService.getLaborsBySkill(skill);
					if (laborsBySkill.isEmpty()) {
						return "No labors found with skill: " + skill;
					}
					StringBuilder laborList = new StringBuilder("Available labors with skill '" + skill + "': ");
					for (Labor labor : laborsBySkill) {
						laborList.append(labor.getName()).append(", ");
					}
					return laborList.toString().replaceAll(", $", ".");
				} catch (Exception e) {
					return "Error while fetching labors by skill: " + e.getMessage();
				}
			} else {
				return "Please specify the skill to get the labor list.";
			}

		case "get labors by location":
			if (extractedData.containsKey("location")) {
				String location = extractedData.get("location");
				try {
					List<Labor> laborsByLocation = laborService.getLaborsByLocation(location);
					if (laborsByLocation.isEmpty()) {
						return "No labors found in location: " + location;
					}
					StringBuilder laborList = new StringBuilder("Available labors in '" + location + "': ");
					for (Labor labor : laborsByLocation) {
						laborList.append(labor.getName()).append(", ");
					}
					return laborList.toString().replaceAll(", $", ".");
				} catch (Exception e) {
					return "Error while fetching labors by location: " + e.getMessage();
				}
			} else {
				return "Please specify the location to get the labor list.";
			}

		case "check booking calendar":
			if (extractedData.containsKey("equipmentId")) {
				Long equipmentId = Long.parseLong(extractedData.get("equipmentId"));
				try {
					Map<String, Object> availabilityData = bookingService.checkAvailability(equipmentId);
					List<String> bookedDates = (List<String>) availabilityData.get("bookedDates");

					if (bookedDates.isEmpty()) {
						return "The equipment with ID " + equipmentId + " is fully available for booking.";
					} else {
						return "The equipment with ID " + equipmentId + " is booked on the following dates: "
								+ String.join(", ", bookedDates);
					}
				} catch (Exception e) {
					return "Error while fetching availability: " + e.getMessage();
				}
			} else {
				return "Please specify the equipment ID to check availability.";
			}

		case "approve booking":
		case "reject booking":
			if (extractedData.containsKey("bookingId") && extractedData.containsKey("status")) {
				Long bookingId = Long.parseLong(extractedData.get("bookingId"));
				String status = extractedData.get("status");
				try {
					String responseMessage = bookingService.approveOrRejectBooking(bookingId, status);
					return "Booking " + status.toLowerCase() + " successfully for ID: " + bookingId;
				} catch (Exception e) {
					return "Error: " + e.getMessage();
				}
			} else {
				return "Please provide booking ID and status (APPROVED/REJECTED).";
			}

		case "mark received":
			if (extractedData.containsKey("bookingId")) {
				Long bookingId = Long.parseLong(extractedData.get("bookingId"));
				try {
					String responseMessage = bookingService.markAsReceived(bookingId);
					return responseMessage;
				} catch (Exception e) {
					return "Error: " + e.getMessage();
				}
			} else {
				return "Please specify the booking ID to mark as received.";
			}

		case "book equipment":
			if (extractedData.containsKey("equipmentId") && extractedData.containsKey("startDate")
					&& extractedData.containsKey("endDate")) {
				return createBookingFromVoice(extractedData, farmerId);
			} else {
				return "Please provide equipment ID and booking dates.";
			}

		case "cancel booking":
			if (extractedData.containsKey("bookingId")) {
				Long bookingId = Long.parseLong(extractedData.get("bookingId"));
				return approveOrRejectBookingFromVoice(bookingId, "REJECTED");
			} else {
				return "Please provide the booking ID to cancel.";
			}

		case "submit feedback":
			if (extractedData.containsKey("rating")) {
				int rating = Integer.parseInt(extractedData.get("rating"));
				String improvements = extractedData.getOrDefault("improvements", "No improvements specified.");
				String issues = extractedData.getOrDefault("issues", "No issues reported.");
				String additionalComments = extractedData.getOrDefault("additionalComments", "No additional comments.");
				boolean recommendation = Boolean.parseBoolean(extractedData.getOrDefault("recommendation", "false"));

				// Use existing method to save feedback
				return submitFeedbackUsingExistingMethod(farmerId, rating, improvements, issues, additionalComments,
						recommendation);
			} else {
				return "Please provide the rating and necessary feedback details.";
			}
		case "get equipment":
			if (extractedData.containsKey("equipmentId")) {
				Long equipmentId = Long.parseLong(extractedData.get("equipmentId"));
				try {
					return "Farmer " + farmerId + ": " + equipmentService.getEquipmentById(equipmentId).toString();
				} catch (Exception e) {
					return "Equipment not found with ID: " + equipmentId;
				}
			} else {
				return "Please specify the equipment ID to fetch details.";
			}

		case "register complaint":
			if (extractedData.containsKey("subject")) {
				String subject = extractedData.get("subject");
				String description = extractedData.getOrDefault("description", "No details provided.");
				return "Complaint registered successfully for farmer " + farmerId + " regarding: " + subject;
			} else {
				return "Please specify the subject for the complaint.";
			}

		case "send invitation email":
			if (extractedData.containsKey("invitedName") && extractedData.containsKey("invitedEmail")) {
				String invitedName = extractedData.get("invitedName");
				String invitedEmail = extractedData.get("invitedEmail");
				Long farmerId1 = Long.parseLong(extractedData.get("id"));

				// Call Invitation Email Service
				emailService.sendHtmlInvitationEmail(invitedName, invitedEmail, farmerId1);
				return "Invitation email sent successfully to " + invitedEmail;
			} else {
				return "Please specify the invited person's name and email.";
			}
		default:
			return commandResponses.getOrDefault(command,
					"I'm here to assist! Try saying 'help' to see available commands. 😊");
		}
	}

	// ✅ Convert word numbers to digits
	private String convertWordToNumber(String word) {
		switch (word.toLowerCase()) {
		case "one":
			return "1";
		case "two":
			return "2";
		case "three":
			return "3";
		case "four":
			return "4";
		case "five":
			return "5";
		case "six":
			return "6";
		case "seven":
			return "7";
		case "eight":
			return "8";
		case "nine":
			return "9";
		case "ten":
			return "10";
		default:
			return word; // Return as is if it's already numeric
		}
	}

	// ✅ Parse JSON data to extract farmerId
	public Map<String, Object> parseJsonData(String jsonData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonData, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	// ✅ Use Existing FeedbackService Method to Save Feedback
	private String submitFeedbackUsingExistingMethod(Long farmerId, int rating, String improvements, String issues,
			String additionalComments, boolean recommendation) {
		// Prepare JSON-like structure to mimic API request
		String feedbackRequestJson = "{" + "\"farmer\": {\"id\": " + farmerId + "}," + "\"rating\": " + rating + ","
				+ "\"improvements\": \"" + improvements + "\"," + "\"issues\": \"" + issues + "\","
				+ "\"additionalComments\": \"" + additionalComments + "\"," + "\"recommendation\": " + recommendation
				+ "}";

		// Call existing API using voiceNavigationService or equivalent method
		return "Feedback submitted successfully for farmer ID: " + farmerId;
	}

	private String processEquipmentBooking(Map<String, String> extractedData, Long farmerId) {
		try {
			Long equipmentId = Long.parseLong(extractedData.get("equipmentId"));
			String startDate = extractedData.get("startDate");
			String endDate = extractedData.get("endDate");

			Farmer farmer2 = farmerService.getFarmerById(farmerId);

			// Prepare Booking Request
			// Booking booking =
			// Booking.builder().equipment(equipmentService.getEquipmentById(equipmentId))
			// .borrower(farmer2).startDate(LocalDate.parse(startDate)).endDate(LocalDate.parse(endDate))
			// .paymentMethod("COD").status("PENDING").build();

			// Save Booking
			Booking savedBooking = bookingService.createBooking(equipmentId, farmerId, startDate, endDate, "COD");

			return "Booking created successfully. Booking ID: " + savedBooking.getId() + ", Status: "
					+ savedBooking.getStatus();
		} catch (Exception e) {
			return "Error while booking equipment. " + e.getMessage();
		}
	}

	private String createBookingFromVoice(Map<String, String> extractedData, Long farmerId) {
		try {
			Long equipmentId = Long.parseLong(extractedData.get("equipmentId"));
			String startDate = extractedData.get("startDate");
			String endDate = extractedData.get("endDate");

			// Call existing createBooking() from BookingService
			Booking booking = bookingService.createBooking(equipmentId, farmerId, startDate, endDate, "COD");
			return "Booking created successfully. Booking ID: " + booking.getId() + ", Status: " + booking.getStatus();
		} catch (Exception e) {
			return "Error while booking equipment. " + e.getMessage();
		}
	}

	private String approveOrRejectBookingFromVoice(Long bookingId, String status) {
		try {
			// Call existing approveOrRejectBooking() from BookingService
			return bookingService.approveOrRejectBooking(bookingId, status);
		} catch (Exception e) {
			return "Error while updating booking status. " + e.getMessage();
		}
	}

	private String markAsReceivedFromVoice(Long bookingId) {
		try {
			// Call existing markAsReceived() from BookingService
			return bookingService.markAsReceived(bookingId);
		} catch (Exception e) {
			return "Error while marking equipment as received. " + e.getMessage();
		}
	}

}
