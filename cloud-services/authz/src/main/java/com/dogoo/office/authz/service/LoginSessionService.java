package com.dogoo.office.authz.service;

import com.dogoo.office.authz.entry.LoginSessionEntry;
import com.dogoo.office.authz.exception.InvalidTokenException;
import com.dogoo.office.authz.exception.TokenRefreshException;
import com.dogoo.office.authz.repo.LoginSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginSessionService {

    @Value("${dogoo.app.jwtRefreshExpirationMins}")
    private Long refreshTokenDurationMins;

    private final LoginSessionRepository loginSessionRepo;

    public LoginSessionService(LoginSessionRepository loginSessionRepo) {
        this.loginSessionRepo = loginSessionRepo;
    }

    public LoginSessionEntry createLoginSession(String agent,
                                                String origin,
                                                Long userId,
                                                String loginType) {

        LoginSessionEntry entry = new LoginSessionEntry();

        Instant now = Instant.now();

        entry.setUserId(userId);
        entry.setAgent(agent);
        entry.setOrigin(origin);
        entry.setDeviceId(UUID.randomUUID());
        entry.setLoginDate(now);
        entry.setLogoutDate(now);
        entry.setRemovedDate(now);
        entry.setExpiryDate(Instant.now().plus(refreshTokenDurationMins, ChronoUnit.MINUTES));
        entry.setStatus(1);
        entry.setLoginType(loginType);

        return loginSessionRepo.save(entry);
    }

    public void removeLoginSession(UUID deviceId, Long userId) {
        Optional<LoginSessionEntry> optEntry = loginSessionRepo.findByDeviceId(deviceId);

        optEntry.map(e -> {
            if (!Objects.equals(e.getUserId(), userId)) {
                throw new InvalidTokenException("DeviceToken", "Not match with userId");
            }

            if (e.getStatus() == 1) {
                e.setRemovedDate(Instant.now());
                e.setStatus(-1);

                loginSessionRepo.save(e);
            }

            return e;
        }).orElseThrow(() -> new InvalidTokenException("Token", "Invalid"));

    }

    public void logoutSession(UUID deviceId) {
        Optional<LoginSessionEntry> optEntry = loginSessionRepo.findByDeviceId(deviceId);

        optEntry.map(e -> {
            if (e.getStatus() == 1) {
                e.setLogoutDate(Instant.now());
                e.setStatus(2);

                loginSessionRepo.save(e);
            }

            return e;
        }).orElseThrow(() -> new InvalidTokenException("Token", "Invalid"));

    }

    public void verifyRefreshToken(UUID deviceId, long userId) {
        Optional<LoginSessionEntry> optEntry = loginSessionRepo.findByDeviceId(deviceId);

        optEntry.orElseThrow(() -> new InvalidTokenException("Token", "Invalid refresh token"));

        if (optEntry.get().getUserId() != userId) {
            throw new InvalidTokenException("Token", "Refresh token is not match for userId");
        }

        if (optEntry.get().getStatus() != 1) {
            throw new TokenRefreshException("Token", "Refresh token was disabled");
        }

        if (optEntry.get().getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException("Token", "Refresh token was expired. Please make a new sign-in request");
        }
    }

}
