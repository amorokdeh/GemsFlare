package com.gemsflare.gemsflare.item.repository;


import com.gemsflare.gemsflare.item.jpa.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
    Optional<ItemEntity> findByName(String name);
    Optional<ItemEntity> findByNumber(String number);
    Page<ItemEntity> findAll(Pageable pageable);
    Page<ItemEntity> findByNumberIn(List<String> numbers, Pageable pageable);
    Page<ItemEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ItemEntity> findByCategoryContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByCategory(String category);
}
