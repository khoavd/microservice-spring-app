package com.dogoo.office.authz.repo;

import com.dogoo.office.authz.entry.AppRoleEntry;
import com.dogoo.office.authz.entry.RoleEntry;
import com.dogoo.office.authz.models.DGRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppRoleRepository extends JpaRepository<AppRoleEntry, Long> {
    List<AppRoleEntry> findByUserId(Long userId);

    List<AppRoleEntry> findByDogooId(Long dogooId);
}
