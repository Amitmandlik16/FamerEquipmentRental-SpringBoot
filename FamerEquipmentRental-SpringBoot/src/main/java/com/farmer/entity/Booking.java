package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JsonIgnoreProperties(value = { "price", "pricePerDay", "stock", "equipmentCondition", "warranty", "equipmentType",
			"manufactureYear", "usageDuration", "location", "pincode", "longitude", "latitude", "imageIds", })
	@JoinColumn(name = "equipment_id", nullable = false)
	private Equipment equipment;

	@ManyToOne
	@JsonIgnoreProperties(value = { "username", "password", "middleName", "country", "state", "district", "taluka",
			"village", "pincode", "longitude", "latitude", "DOB", "landmark", "totalEquipment", "ratingAsGiver",
			"ratingAsTaker", "totalRentalsGiven", "totalRentalsTaken", "totalRewards", "profileImgId" })
	@JoinColumn(name = "borrower_id", nullable = false)
	private Farmer borrower;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private String paymentMethod;

	@Column(nullable = false)
	private String status; // PENDING, APPROVED, REJECTED, RECEIVED, PAYMENT_COMPLETED
}