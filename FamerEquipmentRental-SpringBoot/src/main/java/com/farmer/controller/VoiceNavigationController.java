package com.farmer.controller;

import com.farmer.service.DeepgramService;
import com.farmer.service.voiceNavigationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/voice-assistant")
public class VoiceNavigationController {

	@Autowired
	private DeepgramService deepgramService;

	@Autowired
	private voiceNavigationService voiceNavigationService;
	@PostMapping("/audio")
	public ResponseEntity<ByteArrayResource> generateSpeech(@RequestParam("file") MultipartFile file,
	                                                        @RequestParam("data") String jsonData) {
	    File convertedFile = null;
	    try {
	        // Parse JSON data to extract farmerId
	        Map<String, Object> requestData = voiceNavigationService.parseJsonData(jsonData);

	        // Get farmerId from parsed data
	        Long farmerId = requestData.containsKey("farmerId") 
	                ? Long.parseLong(requestData.get("farmerId").toString()) 
	                : null;

	        if (farmerId == null) {
	            return ResponseEntity.badRequest()
	                    .body(new ByteArrayResource("Missing farmerId".getBytes()));
	        }

	        // Convert MultipartFile to File
	        convertedFile = convertMultipartFileToFile(file);

	        // Process audio with farmerId
	        return voiceNavigationService.voiceReadProcess(convertedFile, farmerId);

	    } catch (NumberFormatException e) {
	        return ResponseEntity.badRequest()
	                .body(new ByteArrayResource("Invalid farmerId format".getBytes()));
	    } catch (IOException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ByteArrayResource("File processing error".getBytes()));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ByteArrayResource("Unexpected error".getBytes()));
	    } finally {
	        if (convertedFile != null && convertedFile.exists()) {
	            convertedFile.delete();
	        }
	    }
	}

	private File convertMultipartFileToFile(MultipartFile file) throws IOException {
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
		}
		return convFile;
	}
}
