package com.dogoo.office.authz.repo;

import com.dogoo.office.authz.entry.AppRoleEntry;
import com.dogoo.office.authz.entry.LoginSessionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoginSessionRepository extends JpaRepository<LoginSessionEntry, Long> {
    List<LoginSessionEntry> findByUserId(Long userId);

    List<LoginSessionEntry> findByUserIdAndStatus(Long userId, int status);

    Optional<LoginSessionEntry> findByDeviceId(UUID deviceId);

    void deleteByDeviceId(UUID deviceId);
}
