package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "farmer_id", nullable = false)
	@JsonIgnoreProperties(value = { "password", "firstName", "middleName", "lastName", "profileImg",
			"country", "state", "district", "village", "pincode", "longitude", "latitude", "mobileNumber",
			"email", "dob" })
	private Farmer farmer;

	@Column(nullable = false)
	private String text;

	// ✅ Store image IDs as comma-separated string
	@Column(name = "image_ids")
	private String imageIds;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Like> likes;
}
