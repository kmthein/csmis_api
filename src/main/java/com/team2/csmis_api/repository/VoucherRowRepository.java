package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.entity.VoucherRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRowRepository extends JpaRepository<VoucherRow, Integer> {

    Optional<VoucherRow> findById(Integer id);
    @Query("SELECT v FROM PaymentVoucher v WHERE v.isDeleted = false")
    List<PaymentVoucher> findAllNonDeleted();
}
