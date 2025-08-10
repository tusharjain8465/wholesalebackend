package com.example.wholesalesalesbackend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.wholesalesalesbackend.model.Client;
import com.example.wholesalesalesbackend.model.SaleEntry;

import jakarta.transaction.Transactional;

@Repository
public interface SaleEntryRepository extends JpaRepository<SaleEntry, Long> {

       // present
       List<SaleEntry> findByClientOrderBySaleDateTimeDesc(Client client);

       // present
       List<SaleEntry> findByClientAndSaleDateTimeBetweenOrderBySaleDateTimeDesc(Client client, LocalDateTime from,
                     LocalDateTime to);

       List<SaleEntry> findByClientAndSaleDateTimeAfterOrderBySaleDateTimeDesc(Client client, LocalDateTime from);

       List<SaleEntry> findByClientAndSaleDateTimeBeforeOrderBySaleDateTimeDesc(Client client, LocalDateTime to);

       // present
       List<SaleEntry> findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(LocalDateTime from, LocalDateTime to);

       @Query(value = "SELECT t.* FROM public.sale_entry t WHERE DATE(t.sale_date_time) BETWEEN :fromDate AND :toDate "
                     + //
                     "    ORDER BY t.sale_date_time DESC", nativeQuery = true)
       List<SaleEntry> findBySaleDateBetweenOrderBySaleDateTimeDescCustom(
                     @Param("fromDate") LocalDate fromDate,
                     @Param("toDate") LocalDate toDate);

       @Query(value = "SELECT t.* FROM public.sale_entry t WHERE t.client_id =:clientId AND DATE(t.sale_date_time) BETWEEN :fromDate AND :toDate "
                     + //
                     "    ORDER BY t.sale_date_time DESC", nativeQuery = true)
       List<SaleEntry> findByClientIdAndSaleDateBetweenOrderBySaleDateTimeDescCustom(
                     @Param("clientId") Long clientId,
                     @Param("fromDate") LocalDate fromDate,
                     @Param("toDate") LocalDate toDate);

       // present
       List<SaleEntry> findAllByOrderBySaleDateTimeDesc();

       @Query(value = "SELECT SUM(total_price) AS sale, SUM(profit) AS profit " +
                     "FROM sale_entry WHERE sale_date_time BETWEEN :from AND :to", nativeQuery = true)
       ProfitAndSaleProjection getTotalPriceAndProfitBetweenDates(@Param("from") LocalDateTime from,
                     @Param("to") LocalDateTime to);

       @Query(value = "SELECT SUM(total_price) AS sale, SUM(profit) AS profit " +
                     "FROM sale_entry WHERE client_id= :clientId and sale_date_time BETWEEN :from AND :to", nativeQuery = true)
       ProfitAndSaleProjection getTotalPriceAndProfitBetweenDatesByClient(@Param("from") LocalDateTime from,
                     @Param("to") LocalDateTime to,
                     @Param("clientId") Long clientId);

       @Modifying
       @Transactional
       @Query(value = "UPDATE sale_entry SET " +
                     "accessory_name = :accessoryName, " +
                     "sale_date_time = :saleDateTime, " +
                     "total_price = :totalPrice " +
                     "WHERE client_id = :clientId AND " +
                     "id = :saleEntryId", nativeQuery = true)
       int updateSalesByClient(
                     @Param("accessoryName") String accessoryName,
                     @Param("saleDateTime") LocalDateTime saleDateTime,
                     @Param("totalPrice") double totalPrice,
                     @Param("clientId") Long clientId,
                     @Param("saleEntryId") Long saleEntryId);

       List<SaleEntry> findBySaleDateTimeAfterOrderBySaleDateTimeDesc(LocalDateTime from);

       List<SaleEntry> findBySaleDateTimeBeforeOrderBySaleDateTimeDesc(LocalDateTime to);

       @Query(value = "SELECT SUM(total_price) AS sale, SUM(profit) AS profit " +
                     "FROM sale_entry WHERE client_id = :clientId", nativeQuery = true)
       ProfitAndSaleProjection getTotalPriceAndProfitByClient(@Param("clientId") Long clientId);

       @Query(value = "SELECT SUM(total_price) AS sale, SUM(profit) AS profit FROM sale_entry", nativeQuery = true)
       ProfitAndSaleProjection getTotalPriceAndProfit();

       @Query(value = "SELECT SUM(t.total_price) FROM sale_entry t WHERE DATE(t.sale_date_time) < :fromDate ", nativeQuery = true)
       Double getOldBalance(
                     @Param("fromDate") LocalDate fromDate);

       @Query(value = "SELECT SUM(t.total_price) FROM sale_entry t WHERE t.client_id =:clientId AND DATE(t.sale_date_time) < :fromDate ", nativeQuery = true)
       Double getOldBalanceOfClient(@Param("clientId") Long clientId,
                     @Param("fromDate") LocalDate fromDate);

}
