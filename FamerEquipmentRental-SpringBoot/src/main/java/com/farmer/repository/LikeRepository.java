package com.farmer.repository;

import com.farmer.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

	// ✅ Check if post is already liked by the user
	boolean existsByPostIdAndFarmerId(Long postId, Long farmerId);

	// ✅ Count likes by post ID
	long countByPostId(Long postId);
}
