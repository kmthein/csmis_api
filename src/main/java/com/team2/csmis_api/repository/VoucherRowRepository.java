package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.entity.VoucherRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRowRepository extends JpaRepository<VoucherRow, Integer> {

    Optional<VoucherRow> findById(Integer id);
    @Query("SELECT v FROM PaymentVoucher v WHERE v.isDeleted = false")
    List<PaymentVoucher> findAllNonDeleted();


    @Query("SELECT v FROM VoucherRow v WHERE v.dt BETWEEN :startDate AND :endDate")
    List<VoucherRow> findByDtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v FROM VoucherRow v WHERE v.dt BETWEEN :startDate AND :endDate")
    List<VoucherRow> findVoucherRowsByDateRange(String startDate, String endDate);

    @Query(value = """
        SELECT                             
            pv.voucher_no AS vno,
            pv.invoice_for AS invo,
            pv.cashier AS cashier,
            pv.received_by AS rece,
            pv.approved_by AS appro,
            pv.payment_date AS pdate,
            SUM(vr.amount) AS amount,
            pv.status AS status
        FROM\s
            payment_vouchers pv
        JOIN\s
            voucher_rows vr ON pv.id = vr.payment_voucher_id
        WHERE\s
            pv.payment_date BETWEEN :startDate AND :endDate
        GROUP BY\s
          pv.voucher_no, pv.invoice_for,\s
          pv.cashier, pv.received_by, pv.approved_by, pv.payment_date, pv.status
        """, nativeQuery = true)
    List<Object[]> getPaidVoucher(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}
