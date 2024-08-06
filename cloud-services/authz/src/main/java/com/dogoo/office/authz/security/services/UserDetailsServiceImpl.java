package com.dogoo.office.authz.security.services;

import com.dogoo.office.authz.entry.AppRoleEntry;
import com.dogoo.office.authz.entry.UserEntry;
import com.dogoo.office.authz.repo.AppRoleRepository;
import com.dogoo.office.authz.repo.DogooRepository;
import com.dogoo.office.authz.repo.UserRepository;
import com.dogoo.office.authz.security.model.UserDetailsImpl;
import com.dogoo.office.authz.security.model.UserDogoo;
import com.dogoo.office.authz.security.model.UserModel;
import com.dogoo.office.authz.security.model.UserTokenModel;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepo;

    private final AppRoleRepository appRole;

    public UserDetailsServiceImpl(UserRepository userRepo,
                                  AppRoleRepository appRole) {
        this.userRepo = userRepo;
        this.appRole = appRole;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntry user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(mapUserModelFromUserEntry(user));
    }

    public UserDetails retriveUserDetails(UserTokenModel model) {
        return UserDetailsImpl.build(model);
    }

    private UserTokenModel mapUserModelFromUserEntry(UserEntry from) {
        UserTokenModel to = new UserTokenModel();

        List<AppRoleEntry> appRoleEntries = appRole.findByUserId(from.getId());

        to.setId(from.getId());
        to.setUsername(from.getUsername());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setPassword(from.getPassword());
        to.setDogoos(appRoleEntries.stream().map(this::mapUserDogoo).collect(Collectors.toList()));
        to.setRoles(new ArrayList<>());

        return to;
    }

    private UserDogoo mapUserDogoo(AppRoleEntry from) {
        UserDogoo to = new UserDogoo();

        List<AppRoleEntry> appRoleEntries = appRole.findByDogooId(from.getDogoo().getId());

        to.setUuid(from.getDogoo().getUuid().toString());
        to.setRoles(appRoleEntries.stream().map(r -> r.getRole().getName().toString()).collect(Collectors.toList()));

        return to;
    }

}
