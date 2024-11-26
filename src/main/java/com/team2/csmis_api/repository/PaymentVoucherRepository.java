package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.PaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long> {

    // Custom query to find the latest voucher number for the current year and month
    @Query("SELECT pv.voucherNo FROM PaymentVoucher pv " +
            "WHERE pv.voucherNo LIKE CONCAT('CS-', :year, :month, '%') " +
            "ORDER BY pv.voucherNo DESC")
    Optional<String> findLatestVoucherNo(String year, String month);
}
    // Custom query to find the latest voucher number for the current year and month
//    @Query("SELECT pv.voucherNo FROM PaymentVoucher pv " +
//            "WHERE pv.voucherNo LIKE CONCAT('CS-', :year, :month, '%') " +
//            "ORDER BY pv.voucherNo DESC")
//    Optional<String> findLatestVoucherNo(String year, String month);

