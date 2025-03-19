package com.farmer.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDTO {
	private Long id;
	private String fileName;
	private String fileType;
	private Long fileSize;
}
