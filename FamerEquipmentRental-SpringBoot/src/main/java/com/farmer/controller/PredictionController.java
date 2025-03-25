package com.farmer.controller;

import com.farmer.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {

	@Autowired
	private PredictionService predictionService;

	// POST API to predict using model
	@PostMapping
	public String predict(@RequestBody double[] features) {
		return predictionService.predict(features);
	}
}
