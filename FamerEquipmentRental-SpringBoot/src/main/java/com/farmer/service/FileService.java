package com.farmer.service;

import org.springframework.web.multipart.MultipartFile;

import com.farmer.dto.FileResponseDTO;

import java.io.IOException;
import java.util.List;

public interface FileService {
	FileResponseDTO uploadFile(MultipartFile file) throws IOException;

	List<FileResponseDTO> getAllFiles();

	byte[] downloadFile(Long id);
}
