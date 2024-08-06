package com.dogoo.office.authz.entry;


import com.dogoo.office.authz.models.DGRoles;
import jakarta.persistence.*;

@Entity
@Table(name = "authz_roles", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class RoleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private DGRoles name;

    public RoleEntry(DGRoles name) {
        this.name = name;
    }

    public RoleEntry() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DGRoles getName() {
        return name;
    }

    public void setName(DGRoles name) {
        this.name = name;
    }
}
