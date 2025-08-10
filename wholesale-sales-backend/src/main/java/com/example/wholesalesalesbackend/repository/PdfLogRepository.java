package com.example.wholesalesalesbackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.wholesalesalesbackend.model.PdfLog;

@Repository
public interface PdfLogRepository extends JpaRepository<PdfLog, Long> {
}
