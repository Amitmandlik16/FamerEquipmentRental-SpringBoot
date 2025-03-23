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
	private double pricePerDay;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false, length = 500)
	private String description;

	@Column(name = "equipment_condition", nullable = false)
	private String equipmentCondition; // e.g., "New", "Used", "Refurbished"

	@Column(nullable = true)
	private String warranty;

	@Column(nullable = true)
	private String equipmentType;

	@Column(nullable = true)
	private int manufactureYear;

	@Column(nullable = false)
	private String usageDuration;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private String pincode;

	@Column(nullable = false)
	private String longitude;

	@Column(nullable = false)
	private String latitude;

	// ✅ Store image IDs as comma-separated values
	@Column(name = "image_ids", nullable = true)
	private String imageIds;

	// ✅ Add Farmer reference (Owner)
	@ManyToOne
	@JoinColumn(name = "farmer_id", nullable = false)
	private Farmer owner; // ✅ Updated here
}
