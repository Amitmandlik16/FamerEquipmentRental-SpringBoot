package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String brand;

	@Column(nullable = false)
	private double price;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false, length = 500)
	private String description;

	@Column(name = "equipment_condition", nullable = false) // ✅ Added Equipment Condition
	private String equipmentCondition; // e.g., "New", "Used", "Refurbished"

	@Column(nullable = true)
	private String warranty; // e.g., "1 Year"

	@Column(nullable = true)
	private String equipmentType; // e.g., "Tractor", "Harvester"

	@Column(nullable = true)
	private int manufactureYear; // Year of manufacture

	@Column(nullable = false)
	private String usageDuration; // e.g., "200 Hours"

	@Column(nullable = false)
	private String location; // Village, Taluka, District

	@Column(nullable = false)
	private String pincode; // Location Pincode

	@Column(nullable = false)
	private String longitude; // Longitude for map integration

	@Column(nullable = false)
	private String latitude; // Latitude for map integration

	// ✅ Store image IDs as comma-separated values
	@Column(name = "image_ids", nullable = true)
	private String imageIds;

	// ✅ Farmer reference
	@ManyToOne
	@JoinColumn(name = "farmer_id", nullable = false)
	private Farmer farmer;
}
