package com.dogoo.office.authz.entry;


import com.dogoo.office.authz.security.oauth.OAuth2Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "authz_users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    }
)
public class UserEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    //@NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String avatarUrl;

    @Size(max = 500)
    private String name;

    @Size(max = 120)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OAuth2Provider provider;

    public UserEntry() {}

    public UserEntry(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @Size(max = 20) String getPhone() {
        return phone;
    }

    public void setPhone(@Size(max = 20) String phone) {
        this.phone = phone;
    }

    public @Size(max = 500) String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(@Size(max = 500) String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public OAuth2Provider getProvider() {
        return provider;
    }

    public void setProvider(OAuth2Provider provider) {
        this.provider = provider;
    }

    public @Size(max = 500) String getName() {
        return name;
    }

    public void setName(@Size(max = 500) String name) {
        this.name = name;
    }
}
