package com.farmer.service;

import com.farmer.entity.Comment;
import com.farmer.entity.Farmer;
import com.farmer.entity.Like;
import com.farmer.entity.Post;
import com.farmer.repository.CommentRepository;
import com.farmer.repository.FarmerRepository;
import com.farmer.repository.LikeRepository;
import com.farmer.repository.PostRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private FarmerRepository farmerRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	// ✅ Create Post with imageIds
	public Post createPost(String text, Long ownerId, String imageIds) {
		Farmer owner = farmerRepository.findById(ownerId)
				.orElseThrow(() -> new EntityNotFoundException("Farmer not found with ID: " + ownerId));

		// ✅ Validate max 5 image IDs
		if (imageIds != null && !imageIds.isEmpty() && imageIds.split(",").length > 5) {
			throw new IllegalArgumentException("You can only attach up to 5 images per post.");
		}

		Post post = new Post();
		post.setText(text);
		post.setFarmer(owner);
		post.setImageIds(imageIds); // ✅ Store imageIds as comma-separated string

		return postRepository.save(post);
	}

	// ✅ Get All Posts
	public List<Post> getAllPosts() {
		return postRepository.findAll();
	}

	// ✅ Get Posts by Owner ID
	public List<Post> getPostsByOwner(Long ownerId) {
		return postRepository.findByFarmerId(ownerId);
	}

	// ✅ Like a Post
	public Like likePost(Long postId, Long ownerId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		Farmer owner = farmerRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Farmer not found"));

		// ✅ Prevent duplicate like
		if (likeRepository.existsByPostIdAndFarmerId(postId, ownerId)) {
			throw new IllegalStateException("Post already liked by this user.");
		}

		Like like = new Like();
		like.setPost(post);
		like.setFarmer(owner);
		return likeRepository.save(like);
	}

	// ✅ Get Like Count
	public long getLikeCount(Long postId) {
		return likeRepository.countByPostId(postId);
	}

	// ✅ Add a Comment
	public Comment addComment(Long postId, Long ownerId, String text) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		Farmer owner = farmerRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Farmer not found"));

		Comment comment = new Comment();
		comment.setPost(post);
		comment.setFarmer(owner);
		comment.setText(text);
		return commentRepository.save(comment);
	}

	// ✅ Get Comments by Post ID
	public List<Comment> getCommentsByPostId(Long postId) {
		return commentRepository.findByPostId(postId);
	}

	// ✅ Delete a Post
	public void deletePost(Long postId, Long ownerId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		if (post.getFarmer().getId() != ownerId) {
			throw new RuntimeException("Unauthorized deletion");
		}
		postRepository.delete(post);
	}

	// ✅ Delete a Comment
	public void deleteComment(Long commentId, Long ownerId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new RuntimeException("Comment not found"));
		if (comment.getFarmer().getId() != ownerId) {
			throw new RuntimeException("Unauthorized deletion");
		}
		commentRepository.delete(comment);
	}
}
