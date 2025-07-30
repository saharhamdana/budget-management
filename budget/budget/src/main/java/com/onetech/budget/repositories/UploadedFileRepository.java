package com.onetech.budget.repositories;

import com.onetech.budget.models.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    boolean existsByFileName(String fileName);
}
