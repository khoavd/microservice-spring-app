package com.dogoo.office.authz.service;

import com.dogoo.office.authz.entry.UserEntry;
import com.dogoo.office.authz.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<UserEntry> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public UserEntry saveUser(UserEntry entry) {
        return userRepo.save(entry);
    }
}
