package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.PaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Integer> {

    @Query("SELECT MAX(pv.voucherNo) FROM PaymentVoucher pv " +
            "WHERE pv.voucherNo LIKE CONCAT('CS-', :year, :month, '%')")
    Optional<String> findLatestVoucherNo(@Param("year") String year, @Param("month") String month);
    Optional<PaymentVoucher> findByInvoiceFor(String invoiceFor);


}