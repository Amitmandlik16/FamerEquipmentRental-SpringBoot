package com.farmer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Base64;

@Service
public class DeepgramService {

	private static final String API_URL = "https://api.deepgram.com/v1/listen";

	@Value("${deepgram.apikey}")
	private String API_KEY;

	public ResponseEntity<ByteArrayResource> textToSpeech(String text) {
		String ttsUrl = "https://api.deepgram.com/v1/speak?encoding=linear16&sample_rate=16000";

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(ttsUrl);
			post.setHeader("Authorization", "Token " + API_KEY);
			post.setHeader("Content-Type", "application/json");

			// Prepare the JSON payload with the text
			String jsonPayload = "{\"text\": \"" + text + "\"}";
			post.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

			CloseableHttpResponse response = httpClient.execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				byte[] audioBytes = EntityUtils.toByteArray(response.getEntity());

				// Prepare the file for download
				ByteArrayResource resource = new ByteArrayResource(audioBytes);

				return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.wav")
						.contentType(MediaType.parseMediaType("audio/wav")).contentLength(audioBytes.length)
						.body(resource);
			} else {
				return ResponseEntity.status(response.getStatusLine().getStatusCode()).body(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	public String transcribeAudio(File audioFile) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(API_URL);
			post.setHeader("Authorization", "Token " + API_KEY);
			post.setHeader("Content-Type", "audio/wav");

			FileEntity fileEntity = new FileEntity(audioFile, ContentType.create("audio/wav"));
			post.setEntity(fileEntity);

			CloseableHttpResponse response = httpClient.execute(post);
			String jsonResponse = EntityUtils.toString(response.getEntity());

			// Extract transcript
			return extractTranscript(jsonResponse);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error during transcription: " + e.getMessage();
		}
	}

	private String extractTranscript(String jsonResponse) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(jsonResponse);
			JsonNode transcriptNode = root.path("results").path("channels").get(0).path("alternatives").get(0)
					.path("transcript");

			return transcriptNode.asText();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error parsing transcript: " + e.getMessage();
		}
	}
}
