package com.dogoo.office.authz.security.model;

import com.dogoo.office.authz.security.oauth.OAuth2Provider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails, OAuth2User {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private String name;

    private String avatarUrl;

    private OAuth2Provider provider;

    private Collection<? extends GrantedAuthority> authorities;

    private List<UserDogoo> dogoos;

    private Map<String, Object> attributes;

    public UserDetailsImpl() {}

    public UserDetailsImpl(Long id,
                           String username,
                           String email,
                           String password,
                           Collection<? extends GrantedAuthority> authorities,
                           List<UserDogoo> dogoos) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.dogoos = dogoos;
    }

    public static UserDetailsImpl build(UserTokenModel user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getDogoos());
    }

    public static UserDetailsImpl build(UserTokenModel user,
                                        String uuid) {

        boolean contains = user.getDogoos().stream().anyMatch(u -> u.getUuid().equals(uuid));

        if (!contains) {
            throw new RuntimeException("Dogoo context: not in any dogoo");
        }

        Optional<List<String>> roles = user.getDogoos().stream()
                .filter(e -> e.getUuid().contentEquals(uuid))
                .findFirst()
                .map(UserDogoo::getRoles);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles.isPresent()) {
            authorities = roles.get().stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getDogoos());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<UserDogoo> getDogoos() {
        return dogoos;
    }

    public void setDogoos(List<UserDogoo> dogoos) {
        this.dogoos = dogoos;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setName(String name) {
        this.name = name;
    }



    public OAuth2Provider getProvider() {
        return provider;
    }

    public void setProvider(OAuth2Provider provider) {
        this.provider = provider;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
