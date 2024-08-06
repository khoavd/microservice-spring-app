package com.dogoo.office.authz.repo;

import com.dogoo.office.authz.entry.AppRoleEntry;
import com.dogoo.office.authz.entry.DogooEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DogooRepository extends JpaRepository<DogooEntry, Long> {
    Optional<DogooEntry> findByUuid(UUID uuid);
}
