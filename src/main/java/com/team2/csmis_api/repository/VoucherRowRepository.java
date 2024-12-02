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

}
