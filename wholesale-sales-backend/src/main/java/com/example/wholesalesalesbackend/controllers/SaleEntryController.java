package com.example.wholesalesalesbackend.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wholesalesalesbackend.dto.ProfitAndSale;
import com.example.wholesalesalesbackend.dto.SaleAttributeUpdateDTO;
import com.example.wholesalesalesbackend.dto.SaleEntryDTO;
import com.example.wholesalesalesbackend.dto.SaleEntryRequestDTO;
import com.example.wholesalesalesbackend.dto.SaleUpdateRequest;
import com.example.wholesalesalesbackend.model.SaleEntry;
import com.example.wholesalesalesbackend.service.SaleEntryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
public class SaleEntryController {

    @Autowired(required = false)
    private SaleEntryService saleEntryService;

    @PostMapping("/sale-entry/add")
    public ResponseEntity<String> addSaleEntry(@RequestBody @Valid SaleEntryRequestDTO requestDTO) {
        SaleEntry savedEntry = saleEntryService.addSaleEntry(requestDTO);
        return ResponseEntity.ok("added");
    }



    @GetMapping("/all-sales/all")
    public ResponseEntity<List<SaleEntryDTO>> getAllSales() {
        List<SaleEntry> entries = saleEntryService.getAllSales();

        List<SaleEntryDTO> dtos = new ArrayList<>();
        for (SaleEntry sale : entries) {

            SaleEntryDTO dto = new SaleEntryDTO();
            dto.setId(sale.getId());
            dto.setProfit(sale.getProfit());
            dto.setQuantity(sale.getQuantity());
            dto.setClientName(sale.getClient().getName());
            dto.setSaleDateTime(sale.getSaleDateTime());
            dto.setTotalPrice(sale.getTotalPrice());
            dto.setReturnFlag(sale.isReturnFlag());
            dto.setNote(sale.getNote());
            dto.setAccessoryName(sale.getAccessoryName());

            dtos.add(dto);

        }

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<SaleEntry>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {
        return ResponseEntity.ok(saleEntryService.getSalesByDateRange(from, to));
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<List<SaleEntryDTO>> getSalesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(saleEntryService.getSalesEntryDTOByClient(clientId));
    }

    @PutMapping("/by-client/{clientId}")
    public ResponseEntity<String> updateSalesByClient(
            @PathVariable Long clientId, @RequestParam(value = "saleEntryId", required = true) Long saleEntryId,
            @RequestBody SaleUpdateRequest request) {
        saleEntryService.updateSalesByClient(clientId, saleEntryId, request);
        return ResponseEntity.ok("Updated !!!");
    }

    @GetMapping("/by-client-and-date-range")
    public ResponseEntity<List<SaleEntryDTO>> getSalesByClientAndDateRange(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {
        
        List<SaleEntryDTO> sales = saleEntryService.getSalesEntryDTOByClientAndDateRange(clientId, from, to);
        return ResponseEntity.ok(sales);
    }

    @PutMapping("/sale-entry/few-attributes")
    public ResponseEntity<String> updateProfit(@RequestBody SaleAttributeUpdateDTO dto) {
        SaleEntry updated = saleEntryService.updateProfit(dto);
        return ResponseEntity.ok("updated!!!");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<SaleEntryDTO> updateSaleEntry(@PathVariable Long id,
            @RequestBody @Valid SaleEntryDTO requestDTO) {
        SaleEntryDTO updated = saleEntryService.updateSaleEntry(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSaleEntry(@PathVariable Long id) {
        String output = saleEntryService.deleteSaleEntry(id);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/profit/by-date-range")
    public ResponseEntity<ProfitAndSale> getProfitByDateRange(
            @RequestParam (required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam (required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
            @RequestParam (required = false) Long days,
            @RequestParam(required = false) Long clientId) {

        return ResponseEntity.ok(saleEntryService.getTotalProfitByDateRange(from, to, days ,clientId));
    }

}
