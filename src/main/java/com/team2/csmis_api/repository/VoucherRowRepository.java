package com.team2.csmis_api.repository;

import com.team2.csmis_api.entity.VoucherRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRowRepository extends JpaRepository<VoucherRow, Long> {
}
