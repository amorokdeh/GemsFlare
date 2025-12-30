package com.gemsflare.gemsflare.address.repository;

import com.gemsflare.gemsflare.address.jpa.DeliveryAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressEntity, UUID> {
    DeliveryAddressEntity findByUserid(UUID userId);
}