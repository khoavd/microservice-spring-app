package com.dogoo.office.authz.security.model;

import java.util.List;

public class UserTokenModel extends UserModel {
    private String sub;

    private Long iat;

    private Long exp;

    private List<UserDogoo> dogoos;

    private String jti;

    private String iss;

    private List<String> aud;

    private String preferred_username;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public List<UserDogoo> getDogoos() {
        return dogoos;
    }

    public void setDogoos(List<UserDogoo> dogoos) {
        this.dogoos = dogoos;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public List<String> getAud() {
        return aud;
    }

    public void setAud(List<String> aud) {
        this.aud = aud;
    }

    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }
}
