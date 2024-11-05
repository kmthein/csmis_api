package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByPaymentDateBetween(Date startDate, Date endDate);
}
