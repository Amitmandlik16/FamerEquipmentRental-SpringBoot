package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
	@JoinColumn(name = "equipment_id", nullable = false)
	private Equipment equipment;

	@ManyToOne
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