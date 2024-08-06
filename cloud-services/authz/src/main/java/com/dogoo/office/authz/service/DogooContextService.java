package com.dogoo.office.authz.service;

import com.dogoo.office.authz.entry.DogooEntry;
import com.dogoo.office.authz.repo.DogooRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DogooContextService {

    private final DogooRepository dogooRepo;

    public DogooContextService(DogooRepository dogooRepo) {
        this.dogooRepo = dogooRepo;
    }

    public DogooEntry getDogooByUuid(String uuid) {
        return dogooRepo.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new RuntimeException("Dogoo Context: uuid does not exist"));
    }
}
