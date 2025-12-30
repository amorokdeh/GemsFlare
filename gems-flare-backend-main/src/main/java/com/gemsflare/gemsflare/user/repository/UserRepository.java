package com.gemsflare.gemsflare.user.repository;

import com.gemsflare.gemsflare.user.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByNameAndLastname(String name, String lastname);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByTelephone(String telephone);
}