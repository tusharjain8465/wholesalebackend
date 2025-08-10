package com.example.wholesalesalesbackend.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SaleEntryRequestDTO {
    private String accessoryName;
    private Integer quantity;
    private Double totalPrice;
    private Double profit;
    private LocalDateTime saleDateTime;
    private Boolean returnFlag;
    private Long clientId;
    private String note;
}
