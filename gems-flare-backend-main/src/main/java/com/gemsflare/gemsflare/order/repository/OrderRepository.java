package com.gemsflare.gemsflare.order.repository;

import com.gemsflare.gemsflare.order.jpa.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByNumber(String number);
    Optional<OrderEntity> findByTransaction(String transaction);
    Page<OrderEntity> findAll(Pageable pageable);
    Page<OrderEntity> findAllByUserid(UUID userid, Pageable pageable);
}
