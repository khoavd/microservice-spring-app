package com.dogoo.office.authz.entry;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "authz_login_sessions")
public class LoginSessionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private UUID deviceId;

    @Size(max = 500)
    private String agent;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private int status;

    private String loginType;

    @Column
    @Size(max = 250)
    private String origin;

    @Column(nullable = true)
    private Instant removedDate;

    @Column(nullable = true)
    private Instant logoutDate;

    @Column
    private Instant loginDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public @Size(max = 500) String getAgent() {
        return agent;
    }

    public void setAgent(@Size(max = 500) String agent) {
        this.agent = agent;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public @Size(max = 250) String getOrigin() {
        return origin;
    }

    public void setOrigin(@Size(max = 250) String origin) {
        this.origin = origin;
    }

    public Instant getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(Instant removedDate) {
        this.removedDate = removedDate;
    }

    public Instant getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Instant logoutDate) {
        this.logoutDate = logoutDate;
    }

    public Instant getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Instant loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
