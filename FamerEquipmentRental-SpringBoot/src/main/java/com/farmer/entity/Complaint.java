package com.farmer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "complaints")
public class Complaint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JsonIgnoreProperties(value = { "username", "password", "middleName", "country", "state", "district", "taluka",
			"village", "pincode", "longitude", "latitude", "DOB", "landmark", "totalEquipment", "ratingAsGiver",
			"ratingAsTaker", "totalRentalsGiven", "totalRentalsTaken", "totalRewards", "profileImgId" })
	@JoinColumn(name = "farmer_id", nullable = false)
	private Farmer farmer;

	private String subject;
	private String description;
	private LocalDate localDate;
	private LocalTime localTime;

}
