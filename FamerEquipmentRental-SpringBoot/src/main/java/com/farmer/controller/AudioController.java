package com.farmer.controller;

import com.farmer.service.DeepgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/audio")
public class AudioController {

	@Autowired
	private DeepgramService deepgramService;

	@PostMapping("/transcribe")
	public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) {
		try {
			File audioFile = convertMultipartFileToFile(file);
			String result = deepgramService.transcribeAudio(audioFile);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
		}
	}

	@PostMapping("/speak")
	public ResponseEntity<ByteArrayResource> generateSpeech(@RequestParam("text") String text) {
		return deepgramService.textToSpeech(text);
	}

	private File convertMultipartFileToFile(MultipartFile file) throws Exception {
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
		file.transferTo(convFile);
		return convFile;
	}
}
