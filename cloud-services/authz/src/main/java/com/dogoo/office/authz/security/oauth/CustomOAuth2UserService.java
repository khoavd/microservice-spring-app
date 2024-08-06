package com.dogoo.office.authz.security.oauth;

import com.dogoo.office.authz.entry.UserEntry;
import com.dogoo.office.authz.security.model.UserDetailsImpl;
import com.dogoo.office.authz.service.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    private final List<OAuth2UserInfoExtractor> oAuth2UserInfoExtractors;

    public CustomOAuth2UserService(UserService userService,
                                   List<OAuth2UserInfoExtractor> oAuth2UserInfoExtractors) {
        this.userService = userService;
        this.oAuth2UserInfoExtractors = oAuth2UserInfoExtractors;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Optional<OAuth2UserInfoExtractor> oAuth2UserInfoExtractorOptional = oAuth2UserInfoExtractors.stream()
                .filter(oAuth2UserInfoExtractor -> oAuth2UserInfoExtractor.accepts(userRequest))
                .findFirst();
        if (oAuth2UserInfoExtractorOptional.isEmpty()) {
            throw new InternalAuthenticationServiceException("The OAuth2 provider is not supported yet");
        }

        UserDetailsImpl customUserDetails = oAuth2UserInfoExtractorOptional.get().extractUserInfo(oAuth2User);
        UserEntry user = upsertUser(customUserDetails);
        customUserDetails.setId(user.getId());

        return customUserDetails;
    }

    private UserEntry upsertUser(UserDetailsImpl customUserDetails) {
        Optional<UserEntry> userOptional = userService.getUserByUsername(customUserDetails.getUsername());
        UserEntry user;
        if (userOptional.isEmpty()) {
            user = new UserEntry();
            user.setUsername(customUserDetails.getUsername());
            user.setName(customUserDetails.getName());
            user.setEmail(customUserDetails.getEmail());
            user.setAvatarUrl(customUserDetails.getAvatarUrl());
            user.setProvider(customUserDetails.getProvider());
            //user.setRole(WebSecurityConfig.USER);
        } else {
            user = userOptional.get();
            user.setEmail(customUserDetails.getEmail());
            user.setName(customUserDetails.getName());
            user.setAvatarUrl(customUserDetails.getAvatarUrl());
        }
        return userService.saveUser(user);
    }
}
