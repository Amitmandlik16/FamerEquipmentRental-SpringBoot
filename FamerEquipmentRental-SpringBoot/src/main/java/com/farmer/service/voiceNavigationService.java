package com.farmer.service;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class voiceNavigationService {

	@Autowired
	DeepgramService deepgramService;

	// Define expected commands and actions
	private static final Map<String, String> commandResponses = new HashMap<>();

	static {
		commandResponses.put("book equipment", "Booking equipment. Please specify the dates.");
		commandResponses.put("cancel booking", "Booking has been cancelled.");
		commandResponses.put("check availability", "Checking availability. Please wait.");
		commandResponses.put("exit", "Exiting the system. Goodbye!");
	}

	public ResponseEntity<ByteArrayResource> voiceReadProcess(File file) {
		String readText = deepgramService.transcribeAudio(file);
		System.out.println("Read Text: " + readText);

		// Check for a matching command
		String speakText = processCommand(readText.toLowerCase());
		System.out.println("Speak Text: " + speakText);

		// Convert response text to audio
		ResponseEntity<ByteArrayResource> audioResource = deepgramService.textToSpeech(speakText);
		return audioResource;
	}

	private String processCommand(String readText) {
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

		// If similarity is 85% or higher, return the matched command's response
		if (bestScore >= 0.50) {
			return commandResponses.get(bestMatch);
		} else {
			return "Sorry, I didn't understand that. Please try again.";
		}
	}
}
