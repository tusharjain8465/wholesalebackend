package com.example.wholesalesalesbackend.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SaleUpdateRequest {
    private String accessoryName;
    private LocalDateTime saleDateTime;
    private double totalPrice;
}
