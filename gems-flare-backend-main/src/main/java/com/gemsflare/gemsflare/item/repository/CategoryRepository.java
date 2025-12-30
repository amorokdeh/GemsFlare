package com.gemsflare.gemsflare.item.repository;

import com.gemsflare.gemsflare.item.jpa.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findAll();
    Optional<CategoryEntity> findByName(String name);
}
