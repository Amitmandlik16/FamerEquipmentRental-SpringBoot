package com.farmer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmer.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
