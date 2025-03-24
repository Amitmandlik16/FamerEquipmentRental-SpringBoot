package com.farmer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class voiceNavigationService {

	@Autowired
	DeepgramService deepgramService;

	@Autowired
	EquipmentService equipmentService;

	@Autowired
	ComplaintService complaintService;

	// Define expected commands and actions
	private static final Map<String, String> commandResponses = new HashMap<>();

	static {
		commandResponses.put("book equipment", "Booking equipment. Please specify the dates.");
		commandResponses.put("cancel booking", "Booking has been cancelled.");
		commandResponses.put("check availability", "Checking availability. Please wait.");
		commandResponses.put("get equipment", "Fetching equipment details.");
		commandResponses.put("register complaint", "Registering a complaint. Please provide the details.");
		commandResponses.put("exit", "Exiting the system. Goodbye!");
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

		// If similarity is 50% or higher, process the command
		if (bestScore >= 0.50) {
			// Extract relevant data if matched
			Map<String, String> extractedData = extractData(readText);

			if (!extractedData.isEmpty()) {
				return performAction(bestMatch, extractedData, farmerId);
			} else {
				// Command matched but no data extracted
				return "Please provide the correct format for your request.";
			}
		} else {
			return "Sorry, I didn't understand that. Try using standard commands.";
		}
	}

	// ✅ Extract relevant data (like equipment ID or complaint details)
	private Map<String, String> extractData(String readText) {
		Map<String, String> extractedData = new HashMap<>();

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

		return extractedData;
	}

	// ✅ Perform action based on matched command and extracted data
	private String performAction(String command, Map<String, String> extractedData, Long farmerId) {
		switch (command) {
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

		default:
			return commandResponses.getOrDefault(command, "Sorry, I didn't understand that.");
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
}
