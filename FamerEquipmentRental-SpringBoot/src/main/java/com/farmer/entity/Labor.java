package com.farmer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "labors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Labor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String skills; // e.g., Ploughing, Harvesting, etc.

    @Column(nullable = false)
    private int experience; // In years

    @Column(nullable = false)
    private double pricePerDay;

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

    // ✅ Authentication for laborer login
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
}
