package com.farmer.repository;

import com.farmer.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	// ✅ Fetch posts by specific Farmer ID
	List<Post> findByFarmerId(Long farmerId);
}
