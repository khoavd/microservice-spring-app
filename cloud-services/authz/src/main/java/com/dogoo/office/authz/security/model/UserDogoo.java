package com.dogoo.office.authz.security.model;

import java.util.List;

public class UserDogoo {

    private String uuid;

    private List<String> roles;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
