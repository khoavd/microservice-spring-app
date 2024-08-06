package com.dogoo.office.authz.entry;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "authz_dogoo", uniqueConstraints = { @UniqueConstraint(columnNames = { "uuid" }) })
public class DogooEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 500)
    private String name;

    @Column(unique = true)
    private UUID uuid;

    public DogooEntry() {}

    public DogooEntry(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank @Size(max = 500) String getName() {
        return name;
    }

    public void setName(@NotBlank @Size(max = 500) String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
