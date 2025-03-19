package com.farmer.service;

import com.farmer.entity.Complaint;
import com.farmer.repository.ComplaintRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintService {

	@Autowired
	private ComplaintRepository complaintRepository;

	// Submit a new complaint
	public Complaint submitComplaint(Complaint complaint) {
		complaint.setLocalDate(LocalDate.now());
		complaint.setLocalTime(LocalTime.now());
		return complaintRepository.save(complaint);
	}

	// Get all complaints
	public List<Complaint> getAllComplaints() {
		return complaintRepository.findAll();
	}
}
