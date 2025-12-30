package com.gemsflare.gemsflare.invoice.repository;

import com.gemsflare.gemsflare.invoice.jpa.InvoiceCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceCounterRepository extends JpaRepository<InvoiceCounterEntity, UUID> {

    Optional<InvoiceCounterEntity> findByDate(LocalDate date);
}
