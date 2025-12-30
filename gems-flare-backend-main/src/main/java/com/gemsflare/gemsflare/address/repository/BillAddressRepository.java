package com.gemsflare.gemsflare.address.repository;

import com.gemsflare.gemsflare.address.jpa.BillAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BillAddressRepository extends JpaRepository<BillAddressEntity, UUID> {
    BillAddressEntity findByUserid(UUID userId);
}