package com.farmer.service;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.tensorflow.engine.TfNDArray;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PredictionService {

	public String predict(double[] features) {
		try {
			// Path to SavedModel directory
			Path modelPath = Paths.get("src/main/resources/models/saved_model");

			Criteria<double[], Integer> criteria = Criteria.builder().setTypes(double[].class, Integer.class)
					.optModelPath(modelPath).optEngine("TensorFlow").build();

			try (Model model = ModelZoo.loadModel(criteria);
					Predictor<double[], Integer> predictor = model.newPredictor(new SavedModelTranslator())) {

				int prediction = predictor.predict(features);
				return "Prediction: " + prediction;
			}

		} catch (ModelException | TranslateException | IOException e) {
			e.printStackTrace();
			return "Error occurred while predicting: " + e.getMessage();
		}
	}

	// More robust translator for SavedModel
	private static class SavedModelTranslator implements Translator<double[], Integer> {
		@Override
		public NDList processInput(TranslatorContext ctx, double[] input) {
			NDManager manager = ctx.getNDManager();
			NDArray array = manager.create(input);
			array = array.reshape(1, input.length);
			return new NDList(array);
		}

		@Override
		public Integer processOutput(TranslatorContext ctx, NDList list) {
			NDArray probabilities = list.singletonOrThrow();
			return (int) probabilities.argMax().getLong();
		}

		@Override
		public Batchifier getBatchifier() {
			return Batchifier.STACK;
		}
	}
}