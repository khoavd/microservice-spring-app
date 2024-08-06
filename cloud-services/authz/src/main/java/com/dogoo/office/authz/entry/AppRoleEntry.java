package com.dogoo.office.authz.entry;


import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "authz_app_role")
public class AppRoleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntry user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dogoo_id", nullable = false, referencedColumnName = "uuid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DogooEntry dogoo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "name")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RoleEntry role;

    public AppRoleEntry() {}

    public AppRoleEntry(UserEntry user, DogooEntry dogoo, RoleEntry role) {
        this.user = user;
        this.dogoo = dogoo;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntry getUser() {
        return user;
    }

    public void setUser(UserEntry user) {
        this.user = user;
    }

    public DogooEntry getDogoo() {
        return dogoo;
    }

    public void setDogoo(DogooEntry dogoo) {
        this.dogoo = dogoo;
    }

    public RoleEntry getRole() {
        return role;
    }

    public void setRole(RoleEntry role) {
        this.role = role;
    }
}
