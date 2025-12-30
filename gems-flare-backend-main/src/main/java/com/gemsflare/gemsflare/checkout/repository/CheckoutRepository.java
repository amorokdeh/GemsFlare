package com.gemsflare.gemsflare.checkout.repository;

import com.gemsflare.gemsflare.checkout.jpa.CheckoutEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutEntity, UUID> {

    Optional<CheckoutEntity> findByNumber(String number);
    Optional<CheckoutEntity> findByUserid(UUID userid);
    Page<CheckoutEntity> findAll(Pageable pageable);
    List<CheckoutEntity> findByDateBefore(Date date);

}
