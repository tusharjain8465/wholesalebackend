package com.example.wholesalesalesbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wholesalesalesbackend.dto.ProfitAndSale;
import com.example.wholesalesalesbackend.dto.SaleAttributeUpdateDTO;
import com.example.wholesalesalesbackend.dto.SaleEntryDTO;
import com.example.wholesalesalesbackend.dto.SaleEntryRequestDTO;
import com.example.wholesalesalesbackend.dto.SaleUpdateRequest;
import com.example.wholesalesalesbackend.model.Client;
import com.example.wholesalesalesbackend.model.SaleEntry;
import com.example.wholesalesalesbackend.repository.ClientRepository;
import com.example.wholesalesalesbackend.repository.ProfitAndSaleProjection;
import com.example.wholesalesalesbackend.repository.SaleEntryRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleEntryService {

    @Autowired
    private SaleEntryRepository saleEntryRepository;

    @Autowired
    private ClientRepository clientRepository;

    public SaleEntry addSaleEntry(SaleEntryRequestDTO dto) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Determine return flag boolean
        boolean isReturn = Boolean.TRUE.equals(dto.getReturnFlag());

        // Add accessory name prefix
        String accessoryName = isReturn
                ? "RETURN -> " + dto.getAccessoryName()
                : "ADD -> " + dto.getAccessoryName();

        // Ensure totalPrice and profit sign based on returnFlag
        Double totalPrice = dto.getTotalPrice() != null ? dto.getTotalPrice() : 0;
        Double profit = dto.getProfit() != null ? dto.getProfit() : 0;

        if (isReturn) {
            // For returns, totalPrice and profit must be negative
            totalPrice = totalPrice > 0 ? -totalPrice : totalPrice;
            profit = profit > 0 ? -profit : profit;
        } else {
            // For sales, totalPrice and profit must be positive
            totalPrice = totalPrice < 0 ? -totalPrice : totalPrice;
            profit = profit < 0 ? -profit : profit;
        }

        SaleEntry saleEntry = SaleEntry.builder()
                .accessoryName(accessoryName)
                .quantity(dto.getQuantity())
                .totalPrice(totalPrice)
                .profit(profit)
                .saleDateTime(dto.getSaleDateTime())
                .note(dto.getNote())
                .returnFlag(isReturn)
                .client(client)
                .build();

        return saleEntryRepository.save(saleEntry);
    }

    public List<SaleEntry> getSalesByClientAndDateRange(Long clientId, LocalDateTime from, LocalDateTime to) {

        List<SaleEntry> entries = new ArrayList<>();

        if (clientId != null) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            if (from != null && to != null) {
                entries = saleEntryRepository.findByClientAndSaleDateTimeBetweenOrderBySaleDateTimeDesc(client, from,
                        to);
            } else if (from != null) {
                entries = saleEntryRepository.findByClientAndSaleDateTimeAfterOrderBySaleDateTimeDesc(client, from);
            } else if (to != null) {
                entries = saleEntryRepository.findByClientAndSaleDateTimeBeforeOrderBySaleDateTimeDesc(client, to);
            } else {
                entries = saleEntryRepository.findByClientOrderBySaleDateTimeDesc(client);
            }

        } else {
            // For all clients
            if (from != null && to != null) {
                entries = saleEntryRepository.findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(from, to);
            } else if (from != null) {
                entries = saleEntryRepository.findBySaleDateTimeAfterOrderBySaleDateTimeDesc(from);
            } else if (to != null) {
                entries = saleEntryRepository.findBySaleDateTimeBeforeOrderBySaleDateTimeDesc(to);
            } else {
                entries = saleEntryRepository.findAllByOrderBySaleDateTimeDesc();
            }
        }

        return entries;

    }

    public List<SaleEntryDTO> getSalesEntryDTOByClientAndDateRange(Long clientId, LocalDateTime from,
            LocalDateTime to) {

        List<SaleEntry> entries = new ArrayList<>();
        List<SaleEntryDTO> dtos = new ArrayList<>();

        if (clientId != null) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            if (from != null && to != null) {
                entries = saleEntryRepository.findByClientAndSaleDateTimeBetweenOrderBySaleDateTimeDesc(client, from,
                        to);
            } else if (from != null) {
                entries = saleEntryRepository.findByClientAndSaleDateTimeAfterOrderBySaleDateTimeDesc(client, from);
            } else if (to != null) {
                entries = saleEntryRepository.findByClientAndSaleDateTimeBeforeOrderBySaleDateTimeDesc(client, to);
            } else {
                entries = saleEntryRepository.findByClientOrderBySaleDateTimeDesc(client);
            }

        } else {
            // For all clients
            if (from != null && to != null) {
                entries = saleEntryRepository.findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(from, to);
            } else if (from != null) {
                entries = saleEntryRepository.findBySaleDateTimeAfterOrderBySaleDateTimeDesc(from);
            } else if (to != null) {
                entries = saleEntryRepository.findBySaleDateTimeBeforeOrderBySaleDateTimeDesc(to);
            } else {
                entries = saleEntryRepository.findAllByOrderBySaleDateTimeDesc();
            }
        }

        for (SaleEntry sale : entries) {

            SaleEntryDTO dto = new SaleEntryDTO();
            dto.setId(sale.getId());
            dto.setProfit(sale.getProfit());
            dto.setQuantity(sale.getQuantity());
            dto.setClientName(sale.getClient().getName());
            dto.setSaleDateTime(sale.getSaleDateTime());
            dto.setTotalPrice(sale.getTotalPrice());
            dto.setReturnFlag(sale.isReturnFlag());
            dto.setAccessoryName(sale.getAccessoryName());
            dto.setNote(sale.getNote());

            dtos.add(dto);

        }

        return dtos;

    }

    public SaleEntry updateProfit(SaleAttributeUpdateDTO dto) {
        SaleEntry entry = saleEntryRepository.findById(dto.getSaleEntryId())
                .orElseThrow(() -> new RuntimeException("SaleEntry not found with id: " + dto.getSaleEntryId()));

        entry.setProfit(dto.getProfit());
        entry.setAccessoryName(dto.getAccessory());
        entry.setTotalPrice(dto.getTotalPrice());

        return saleEntryRepository.save(entry);
    }

    public List<SaleEntry> getSalesEntryByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return saleEntryRepository.findByClientOrderBySaleDateTimeDesc(client);
    }

    public List<SaleEntryDTO> getSalesEntryDTOByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<SaleEntry> entries = saleEntryRepository.findByClientOrderBySaleDateTimeDesc(client);

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

        return dtos;
    }

    @Transactional
    public int updateSalesByClient(Long clientId, Long saleEntryId, SaleUpdateRequest saleUpdateRequest) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        String newAccessoryName = saleUpdateRequest.getAccessoryName();
        LocalDateTime now = saleUpdateRequest.getSaleDateTime();
        Double newTotalPrice = saleUpdateRequest.getTotalPrice();

        return saleEntryRepository.updateSalesByClient(newAccessoryName, now, newTotalPrice, client.getId(),
                saleEntryId);
    }

    public List<SaleEntry> getSalesByDateRange(LocalDateTime from, LocalDateTime to) {
        return saleEntryRepository.findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(from, to);
    }

    public List<SaleEntry> getAllSales() {
        return saleEntryRepository.findAllByOrderBySaleDateTimeDesc();
    }

    public SaleEntryDTO updateSaleEntry(Long id, SaleEntryDTO updatedEntry) {
        SaleEntry existing = saleEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SaleEntry not found"));

        existing
                .setAccessoryName(
                        Boolean.TRUE.equals(updatedEntry.getReturnFlag())
                                ? "RETURN -> " + updatedEntry.getAccessoryName()
                                : "ADD -> " + updatedEntry.getAccessoryName());
        existing.setQuantity(updatedEntry.getQuantity());
        existing.setTotalPrice(updatedEntry.getTotalPrice());
        existing.setReturnFlag(updatedEntry.getReturnFlag());
        existing.setSaleDateTime(updatedEntry.getSaleDateTime());
        existing.setProfit(updatedEntry.getProfit());
        existing.setNote(updatedEntry.getNote());
        existing.setAccessoryName(updatedEntry.getAccessoryName());

        if (updatedEntry.getClientName() != null) {
            Client client = clientRepository.findByName(updatedEntry.getClientName())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            existing.setClient(client);
        }

        existing = saleEntryRepository.save(existing);

        SaleEntryDTO dto = new SaleEntryDTO();
        dto.setId(id);
        dto.setProfit(existing.getProfit());
        dto.setQuantity(existing.getQuantity());
        dto.setClientName(existing.getClient().getName());
        dto.setSaleDateTime(existing.getSaleDateTime());
        dto.setTotalPrice(existing.getTotalPrice());
        dto.setReturnFlag(existing.isReturnFlag());
        dto.setNote(existing.getNote());
        dto.setAccessoryName(existing.getAccessoryName());

        return dto;
    }

    public String deleteSaleEntry(Long id) {
        SaleEntry existing = saleEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SaleEntry not found"));
        saleEntryRepository.delete(existing);
        return "Deleted !!!";
    }

    public ProfitAndSale getTotalProfitByDateRange(LocalDateTime from, LocalDateTime to, Long days, Long clientId) {

        // If 'days' is provided but no from/to, calculate date range
        if (days != null && from == null && to == null) {
            to = LocalDateTime.now();
            from = to.minusDays(days);
        }

        ProfitAndSaleProjection result;

        // If clientId is provided, use client-specific repository method
        if (clientId != null) {
            result = saleEntryRepository.getTotalPriceAndProfitBetweenDatesByClient(from, to, clientId);
        } else {
            result = saleEntryRepository.getTotalPriceAndProfitBetweenDates(from, to);
        }

        // Handle null projection result
        if (result == null) {
            return new ProfitAndSale(0.0, 0.0);
        }

        return new ProfitAndSale(result.getSale(), result.getProfit());
    }

    public ProfitAndSale getTotalSaleDateRange(LocalDateTime from, LocalDateTime to, Long clientId) {
        ProfitAndSaleProjection result = null;

        boolean isDateRangeProvided = (from != null && to != null);
        boolean isClientProvided = (clientId != null);

        if (isDateRangeProvided && isClientProvided) {
            // Case 1: Both dates and client ID are provided
            result = saleEntryRepository.getTotalPriceAndProfitBetweenDatesByClient(from, to, clientId);
        } else if (isDateRangeProvided) {
            // Case 2: Only dates provided
            result = saleEntryRepository.getTotalPriceAndProfitBetweenDates(from, to);
        } else if (isClientProvided) {
            // Case 3: Only client ID provided
            result = saleEntryRepository.getTotalPriceAndProfitByClient(clientId);
        } else {
            // Case 4: No filters
            result = saleEntryRepository.getTotalPriceAndProfit();
        }

        if (result == null) {
            return new ProfitAndSale(0.0, 0.0);
        }

        return new ProfitAndSale(result.getSale(), result.getProfit());
    }

}
