package com.farmer.repository;

import com.farmer.entity.Labor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LaborRepository extends JpaRepository<Labor, Long> {

	List<Labor> findBySkillsContaining(String skill);

	List<Labor> findByLocationContaining(String location);

	Labor findByEmail(String email);
}
