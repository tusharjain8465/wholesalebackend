package com.example.wholesalesalesbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private LocalDateTime generatedAt;

    private String sentToClient; // Optional: client name or WhatsApp number

    private String filePath; // Relative or full path
}

