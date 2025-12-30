package com.gemsflare.gemsflare.invoice.repository;

import com.gemsflare.gemsflare.invoice.jpa.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {

    Optional<InvoiceEntity> findByNumber(String number);
    Optional<InvoiceEntity> findByOrdernumber(String ordernumber);
    Page<InvoiceEntity> findAll(Pageable pageable);
}
