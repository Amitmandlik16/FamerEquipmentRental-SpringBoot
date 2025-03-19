package com.farmer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.farmer.dto.FileResponseDTO;
import com.farmer.service.FileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@PostMapping("/upload")
	public ResponseEntity<FileResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		FileResponseDTO response = fileService.uploadFile(file);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/all")
	public ResponseEntity<List<FileResponseDTO>> getAllFiles() {
		List<FileResponseDTO> files = fileService.getAllFiles();
		return ResponseEntity.ok(files);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
		byte[] fileData = fileService.downloadFile(id);

		FileResponseDTO fileDetails = fileService.getAllFiles().stream().filter(f -> f.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RuntimeException("File not found"));

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileDetails.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDetails.getFileName() + "\"")
				.body(fileData);
	}
}
