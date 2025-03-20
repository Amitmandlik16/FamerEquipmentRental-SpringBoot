package com.farmer.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmer.entity.Comment;
import com.farmer.entity.Post;
import com.farmer.service.PostService;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

	@Autowired
	private PostService postService;

	// ✅ Create Post API (Send imageIds as comma-separated string)
	@PostMapping("/create")
	public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> request) {
		String text = (String) request.get("text");
		Long ownerId = Long.parseLong(request.get("ownerId").toString());
		String imageIds = (String) request.get("imageIds"); // ✅ Comma-separated image IDs
		return ResponseEntity.ok(postService.createPost(text, ownerId, imageIds));
	}

	// ✅ Get All Posts API
	@GetMapping("/all")
	public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
		List<Post> posts = postService.getAllPosts();
		List<Map<String, Object>> postList = posts.stream().map(post -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", post.getId());
			map.put("text", post.getText());
			map.put("ownerId", post.getFarmer().getId());
			map.put("imageUrls", getImageUrls(post.getImageIds())); // ✅ Generate URLs dynamically
			map.put("createdAt", post.getCreatedAt());
			map.put("likes", post.getLikes().size());
			map.put("comments", post.getComments().size());
			return map;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(postList);
	}

	// ✅ Get Posts by Farmer API
	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<List<Map<String, Object>>> getPostsByOwner(@PathVariable Long ownerId) {
		List<Post> posts = postService.getPostsByOwner(ownerId);
		List<Map<String, Object>> postList = posts.stream().map(post -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", post.getId());
			map.put("text", post.getText());
			map.put("ownerId", post.getFarmer().getId());
			map.put("imageUrls", getImageUrls(post.getImageIds()));
			map.put("createdAt", post.getCreatedAt());
			map.put("likes", post.getLikes().size());
			map.put("comments", post.getComments().size());
			return map;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(postList);
	}

	// ✅ Like Post API
	@PostMapping("/like/{postId}/{ownerId}")
	public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId, @PathVariable Long ownerId) {
		postService.likePost(postId, ownerId);
		long likeCount = postService.getLikeCount(postId);
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Post liked successfully!");
		response.put("likeCount", likeCount);
		return ResponseEntity.ok(response);
	}

	// ✅ Add Comment API
	@PostMapping("/addComment")
	public ResponseEntity<Comment> addComment(@RequestBody Map<String, Object> request) {
		Long postId = Long.parseLong(request.get("postId").toString());
		Long ownerId = Long.parseLong(request.get("ownerId").toString());
		String text = (String) request.get("text");
		return ResponseEntity.ok(postService.addComment(postId, ownerId, text));
	}

	// ✅ Get Comments by Post ID API
	@GetMapping("/comments/{postId}")
	public ResponseEntity<List<Map<String, Object>>> getCommentsByPostId(@PathVariable Long postId) {
		List<Comment> comments = postService.getCommentsByPostId(postId);
		List<Map<String, Object>> commentList = comments.stream().map(comment -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", comment.getId());
			map.put("text", comment.getText());
			map.put("ownerId", comment.getFarmer().getId());
			map.put("createdAt", comment.getCreatedAt());
			return map;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(commentList);
	}

	// ✅ Delete Post API
	@DeleteMapping("/delete/{postId}/{ownerId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId, @PathVariable Long ownerId) {
		postService.deletePost(postId, ownerId);
		return ResponseEntity.noContent().build();
	}

	// ✅ Delete Comment API
	@DeleteMapping("/comment/delete/{commentId}/{ownerId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long ownerId) {
		postService.deleteComment(commentId, ownerId);
		return ResponseEntity.noContent().build();
	}

	// ✅ Helper method to generate image URLs
	private List<String> getImageUrls(String imageIds) {
		if (imageIds == null || imageIds.isEmpty()) {
			return List.of();
		}
		return Arrays.stream(imageIds.split(","))
				.map(id -> "/api/files/download/" + id) // ✅ Generate URL for each imageId
				.collect(Collectors.toList());
	}
}
