package com.dogoo.office.authz.runner;

import com.dogoo.office.authz.entry.AppRoleEntry;
import com.dogoo.office.authz.entry.DogooEntry;
import com.dogoo.office.authz.entry.RoleEntry;
import com.dogoo.office.authz.entry.UserEntry;
import com.dogoo.office.authz.models.DGRoles;
import com.dogoo.office.authz.repo.AppRoleRepository;
import com.dogoo.office.authz.repo.DogooRepository;
import com.dogoo.office.authz.repo.RoleRepository;
import com.dogoo.office.authz.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final UserRepository userRepo;

    private final DogooRepository dogooRepo;

    private final RoleRepository roleRepo;

    private final AppRoleRepository appRoleRepo;

    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepository userRepo,
                               DogooRepository dogooRepo,
                               RoleRepository roleRepo,
                               AppRoleRepository appRoleRepo,
                               PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.dogooRepo = dogooRepo;
        this.roleRepo = roleRepo;
        this.appRoleRepo = appRoleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepo.findAll().isEmpty()) {
            return;
        }
        log.info("Database initialized");

        USERS.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
        });
        log.info("user initialized");

        DOGOOS.forEach(dogooRepo::save);
        log.info("DOGOOS initialized");

        ROLES.forEach(roleRepo::save);
        log.info("ROLES initialized");

        APP_ROLES.forEach(appRoleRepo::save);

        log.info("APP_ROLES initialized");
    }

    static UserEntry user = new UserEntry("khoavu", "khoavu@dogoo.vn", "123456");

    static UUID uuidA = UUID.randomUUID();
    static UUID uuidB = UUID.randomUUID();

    static DogooEntry dogooA = new DogooEntry("CTY A", uuidA);
    static DogooEntry dogooB = new DogooEntry("CTY B", uuidB);

    static RoleEntry roleADM = new RoleEntry(DGRoles.ROLE_ADMIN);
    static RoleEntry roleMOD = new RoleEntry(DGRoles.ROLE_MODERATOR);
    static RoleEntry roleUSR = new RoleEntry(DGRoles.ROLE_USER);

    private static final List<DogooEntry> DOGOOS = List.of(
            dogooA,
            dogooB
    );

    private static final List<RoleEntry> ROLES = List.of(
            roleADM, roleMOD, roleUSR
    );

    private static final List<UserEntry> USERS = List.of(
            user
    );

    private static final List<AppRoleEntry> APP_ROLES = List.of(
            new AppRoleEntry(user, dogooA, roleADM),
            new AppRoleEntry(user, dogooB, roleUSR)
    );

}
