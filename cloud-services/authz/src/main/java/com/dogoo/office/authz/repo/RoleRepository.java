package com.dogoo.office.authz.repo;

import com.dogoo.office.authz.entry.RoleEntry;
import com.dogoo.office.authz.models.DGRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntry, Long> {
    Optional<RoleEntry> findByName(DGRoles name);
}
