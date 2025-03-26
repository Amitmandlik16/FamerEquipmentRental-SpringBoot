package com.farmer.repository;

import com.farmer.entity.LaborBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LaborBookingRepository extends JpaRepository<LaborBooking, Long> {

	// ✅ Get Labor Bookings by Labor ID
	List<LaborBooking> findByLaborId(Long laborId);

	List<LaborBooking> findByLaborIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long laborId,
			LocalDate startDate, LocalDate endDate);

	@Query("SELECT b FROM LaborBooking b WHERE b.labor.id = :laborId AND "
			+ "(b.startDate <= :endDate AND b.endDate >= :startDate)")
	List<LaborBooking> findOverlappingBookings(@Param("laborId") Long laborId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("SELECT b.startDate, b.endDate FROM LaborBooking b WHERE b.labor.id = :laborId AND b.status = 'APPROVED'")
	List<Object[]> findBookingDatesByLaborId(@Param("laborId") Long laborId);

}
