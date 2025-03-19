package com.farmer.service;

import com.farmer.entity.Feedback;
import com.farmer.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class FeedbackService {

	@Autowired
	private FeedbackRepository feedbackRepository;

	// Save feedback with the current date and time
	public Feedback saveFeedback(Feedback feedback) {
		feedback.setLocalDate(LocalDate.now());
		feedback.setLocalTime(LocalTime.now());
		return feedbackRepository.save(feedback);
	}

	// Retrieve all feedback records
	public List<Feedback> getAllFeedbacks() {
		return feedbackRepository.findAll();
	}
}
