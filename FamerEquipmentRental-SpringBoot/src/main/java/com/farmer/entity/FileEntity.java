package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String fileName;

	@Column(nullable = false)
	private String fileType;

	@Column(nullable = false)
	private Long fileSize;

	@Lob
	@Column(name = "file_data", columnDefinition = "MEDIUMBLOB")
	private byte[] fileData;

}
