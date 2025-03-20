package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	@JsonIgnore
	private Post post;

	@ManyToOne
	@JsonIgnoreProperties(value = { "password", "firstName", "middleName", "lastName", "profileImg",
			"country", "state", "district", "village", "pincode", "longitude", "latitude", "mobileNumber",
			"email", "dob" })
	@JoinColumn(name = "farmer_id", nullable = false)
	private Farmer farmer;

	@Column(nullable = false)
	private String text;

	@JsonIgnore
	private LocalDateTime createdAt = LocalDateTime.now();
}
