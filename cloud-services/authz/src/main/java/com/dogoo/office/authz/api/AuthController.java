package com.dogoo.office.authz.api;

import com.dogoo.office.authz.entry.*;
import com.dogoo.office.authz.exception.InvalidTokenException;
import com.dogoo.office.authz.models.DGRoles;
import com.dogoo.office.authz.models.request.LoginRequest;
import com.dogoo.office.authz.models.request.LoginSessionRequest;
import com.dogoo.office.authz.models.request.SignupRequest;
import com.dogoo.office.authz.models.response.MessageResponse;
import com.dogoo.office.authz.models.response.TokenRefreshResponse;
import com.dogoo.office.authz.repo.AppRoleRepository;
import com.dogoo.office.authz.repo.RoleRepository;
import com.dogoo.office.authz.repo.UserRepository;
import com.dogoo.office.authz.security.jwt.TokenProvider;
import com.dogoo.office.authz.security.model.UserDetailsImpl;
import com.dogoo.office.authz.security.model.UserTokenModel;
import com.dogoo.office.authz.security.oauth.OAuth2Provider;
import com.dogoo.office.authz.service.CookieService;
import com.dogoo.office.authz.service.DogooContextService;
import com.dogoo.office.authz.service.LoginSessionService;
import com.dogoo.office.authz.service.TokenService;
import com.dogoo.office.authz.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AppRoleRepository appRoleRepository;

    private final PasswordEncoder encoder;

    private final TokenProvider tokenProvider;

    private final LoginSessionService loginSessionService;

    private final CookieService cookieService;

    private final TokenService tokenService;

    private final DogooContextService dogooService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository, AppRoleRepository appRoleRepository,
                          PasswordEncoder encoder,
                          TokenProvider tokenProvider,
                          LoginSessionService loginSessionService,
                          CookieService cookieService, TokenService tokenService, DogooContextService dogooService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.appRoleRepository = appRoleRepository;
        this.encoder = encoder;
        this.tokenProvider = tokenProvider;
        this.loginSessionService = loginSessionService;
        this.cookieService = cookieService;
        this.tokenService = tokenService;
        this.dogooService = dogooService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              @RequestHeader(value = "User-Agent", required = false) String agent,
                                              @RequestHeader(value = "Origin", required = false) String origin) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String jwt = tokenProvider.generate(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        LoginSessionEntry loginSession =
                loginSessionService.createLoginSession(
                        agent,
                        origin,
                        userDetails.getId(),
                        OAuth2Provider.LOCAL.toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.generateTokenCookie(jwt).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.generateRefreshTokenCookie(loginSession.getDeviceId().toString()).toString())
                .body(jwt);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest,
                                          HttpServletRequest request) {

        String dogooContext = request.getHeader(Constants.DOGOO_CONTEXT_HEADER);

        DogooEntry dogooEntry = dogooService.getDogooByUuid(dogooContext);

        if (dogooContext == null || dogooContext.isEmpty()) {
            throw new RuntimeException("Dogoo context: is null");
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone is already in use!"));
        }

        // Create new user's account
        final UserEntry user = new UserEntry(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        user.setPhone(signUpRequest.getPhone());
        user.setProvider(OAuth2Provider.LOCAL);

        Set<String> strRoles = signUpRequest.getRole();
        Set<RoleEntry> roles = new HashSet<>();

        if (strRoles == null) {
            RoleEntry userRole = roleRepository.findByName(DGRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RoleEntry adminRole = roleRepository.findByName(DGRoles.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        RoleEntry modRole = roleRepository.findByName(DGRoles.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        RoleEntry userRole = roleRepository.findByName(DGRoles.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        userRepository.save(user);

        roles.forEach(role -> {
            AppRoleEntry appRole = new AppRoleEntry();

            appRole.setUser(user);
            appRole.setRole(role);
            appRole.setDogoo(dogooEntry);

            appRoleRepository.save(appRole);
        });

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        String refreshToken = tokenService.getTokenByName(request, Constants.DOGOO_REFRESH_TOKEN);

        Optional<String> accessToken = tokenService.parseJwt(request);

        accessToken.orElseThrow(() -> new InvalidTokenException("Token", "is null"));

        String payload = tokenService.getTokenPayload(accessToken.get());

        try {
            UserTokenModel userTokenModel = tokenService.mapJwtToUserModel(payload);

            loginSessionService.verifyRefreshToken(UUID.fromString(refreshToken), userTokenModel.getId());

            String jwt = tokenProvider.generate(UserDetailsImpl.build(userTokenModel));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookieService.generateTokenCookie(jwt).toString())
                    .header(HttpHeaders.SET_COOKIE, cookieService.generateRefreshTokenCookie(refreshToken).toString())
                    .body(new TokenRefreshResponse(jwt, refreshToken));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/terminal-session")
    public ResponseEntity<?> terminalSession(HttpServletRequest request,
                                             @RequestBody LoginSessionRequest sessionRequest) {

        String token = tokenService.getTokenByName(request, Constants.DOGOO_REFRESH_TOKEN);

        if (token.contentEquals(sessionRequest.getDeviceId())) {
            throw new InvalidTokenException("Token", "Can not remove current session");
        }

        Optional<String> accessToken = tokenService.parseJwt(request);

        accessToken.orElseThrow(() -> new InvalidTokenException("Token", "is null"));

        String payload = tokenService.getTokenPayload(accessToken.get());

        try {
            UserTokenModel userTokenModel = tokenService.mapJwtToUserModel(payload);

            loginSessionService.removeLoginSession(UUID.fromString(sessionRequest.getDeviceId()), userTokenModel.getId());

            return ResponseEntity.ok()
                    .body(sessionRequest);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logoutUser(HttpServletRequest httpServletRequest) {

        String token = tokenService.getTokenByName(httpServletRequest, Constants.DOGOO_REFRESH_TOKEN);

        loginSessionService.logoutSession(UUID.fromString(token));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.getCleanTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.getCleanRefreshTokenCookie().toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}
