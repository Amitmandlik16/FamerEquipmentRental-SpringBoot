package com.farmer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.farmer.dto.FileResponseDTO;
import com.farmer.entity.FileEntity;
import com.farmer.repository.FileRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

	private final FileRepository fileRepository;

	@Override
	public FileResponseDTO uploadFile(MultipartFile file) throws IOException {
		FileEntity fileEntity = FileEntity.builder().fileName(file.getOriginalFilename())
				.fileType(file.getContentType()).fileSize(file.getSize()).fileData(file.getBytes()).build();

		fileRepository.save(fileEntity);

		return mapToDTO(fileEntity);
	}

	@Override
	public List<FileResponseDTO> getAllFiles() {
		List<FileEntity> files = fileRepository.findAll();
		return files.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public byte[] downloadFile(Long id) {
		FileEntity fileEntity = fileRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("File not found with id: " + id));
		return fileEntity.getFileData();
	}

	private FileResponseDTO mapToDTO(FileEntity fileEntity) {
		return FileResponseDTO.builder().id(fileEntity.getId()).fileName(fileEntity.getFileName())
				.fileType(fileEntity.getFileType()).fileSize(fileEntity.getFileSize()).build();
	}
}
