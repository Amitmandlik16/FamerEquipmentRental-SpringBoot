package com.farmer.repository;

import com.farmer.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByEquipmentId(Long equipmentId);

	// ✅ Get Booking Requests by Farmer ID
	List<Booking> findByEquipment_Owner_Id(Long farmerId);

}
