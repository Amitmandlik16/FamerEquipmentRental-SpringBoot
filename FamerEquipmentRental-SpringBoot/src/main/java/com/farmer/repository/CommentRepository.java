package com.farmer.repository;

import com.farmer.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	// ✅ Get comments by post ID
	List<Comment> findByPostId(Long postId);
}
