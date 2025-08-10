package com.example.wholesalesalesbackend.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wholesalesalesbackend.dto.ProfitAndSale;
import com.example.wholesalesalesbackend.model.Client;
import com.example.wholesalesalesbackend.model.SaleEntry;
import com.example.wholesalesalesbackend.repository.SaleEntryRepository;
import com.example.wholesalesalesbackend.service.ClientService;
import com.example.wholesalesalesbackend.service.PdfService;
import com.example.wholesalesalesbackend.service.SaleEntryService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired(required = false)
    private SaleEntryService saleEntryService;

    @Autowired(required = false)
    private PdfService pdfService;

    @Autowired(required = false)
    private ClientService clientService;

    @Autowired(required = false)
    SaleEntryRepository saleEntryRepository;

    @GetMapping("/sales")
    public ResponseEntity<byte[]> generateSalesPdf(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime depositDatetime,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) Double oldBalance,
            @RequestParam(required = false, defaultValue = "0") Double depositAmount) throws IOException {

        boolean isAllClient = (clientId == null);
        String clientName;
        List<SaleEntry> sales = new ArrayList<>();
        LocalDate fromLocalDate = from.toLocalDate();
        LocalDate toLocalDate = to.toLocalDate();

        if (!isAllClient) {
            Client client = clientService.getClientById(clientId);
            clientName = client.getName();

            if (oldBalance == null) {

                oldBalance = saleEntryRepository.getOldBalanceOfClient(clientId, fromLocalDate);
            }

            if (from != null && to != null) {

                sales = saleEntryRepository.findByClientIdAndSaleDateBetweenOrderBySaleDateTimeDescCustom(clientId,
                        fromLocalDate, toLocalDate);
            }

        } else {
            clientName = "All_Clients";

            if (oldBalance == null) {

                oldBalance = saleEntryRepository.getOldBalance(fromLocalDate);
            }

            if (from != null && to != null) {

                sales = saleEntryRepository.findBySaleDateBetweenOrderBySaleDateTimeDescCustom(fromLocalDate,
                        toLocalDate);
            }

        }

        ByteArrayInputStream bis = pdfService.generateSalesPdf(
                clientName, sales, fromLocalDate, toLocalDate, isAllClient, depositAmount, depositDatetime, oldBalance);

        byte[] pdfBytes = bis.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename("sales_report_" + clientName + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
