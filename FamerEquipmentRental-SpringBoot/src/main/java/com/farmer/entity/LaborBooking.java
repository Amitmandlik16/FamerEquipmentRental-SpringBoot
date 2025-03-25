package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "labor_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JsonIgnoreProperties(value = { "experience", "location", "pincode", "longitude", "latitude", "imageIds",
			"password" })

	@JoinColumn(name = "labor_id", nullable = false)
	private Labor labor;

	@Column(nullable = false)
	private Long farmerId; // Farmer who booked labor

	@Column(nullable = false)
	private String farmerEmail; // ✅ Add Farmer Email for Notifications

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private String paymentMethod; // COD or Online

	@Column(nullable = false)
	private String status; // PENDING, APPROVED, REJECTED, COMPLETED
}
