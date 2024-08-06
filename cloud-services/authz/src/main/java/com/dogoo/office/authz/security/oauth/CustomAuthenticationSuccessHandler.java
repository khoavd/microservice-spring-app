package com.dogoo.office.authz.security.oauth;

import com.dogoo.office.authz.entry.LoginSessionEntry;
import com.dogoo.office.authz.security.jwt.TokenProvider;
import com.dogoo.office.authz.security.model.UserDetailsImpl;
import com.dogoo.office.authz.service.CookieService;
import com.dogoo.office.authz.service.LoginSessionService;
import com.dogoo.office.authz.util.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    private final LoginSessionService loginSessionService;

    @Value("${dogoo.app.oauth2.redirectUri}")
    private String redirectUri;

    public CustomAuthenticationSuccessHandler(TokenProvider tokenProvider,
                                              LoginSessionService loginSessionService) {
        this.tokenProvider = tokenProvider;
        this.loginSessionService = loginSessionService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = redirectUri.isEmpty() ?
                determineTargetUrl(request, response, authentication) : redirectUri;

        //log.info(request.getServerName());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = tokenProvider.generate(authentication);

        Cookie cookie = new Cookie(Constants.DOGOO_ACCESS_TOKEN, token);

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain(request.getServerName());
        cookie.setMaxAge(Integer.MAX_VALUE);

        LoginSessionEntry loginSession =
                loginSessionService.createLoginSession(
                        request.getHeader(Constants.USER_AGENT),
                        request.getHeader(Constants.ORIGIN),
                        userDetails.getId(),
                        userDetails.getProvider().toString());

        Cookie cookieRefresh = new Cookie(Constants.DOGOO_REFRESH_TOKEN, loginSession.getDeviceId().toString());

        cookieRefresh.setHttpOnly(true);
        cookieRefresh.setPath("/");
        cookieRefresh.setSecure(true);
        cookieRefresh.setDomain(request.getServerName());
        cookieRefresh.setMaxAge(Integer.MAX_VALUE);

        response.addCookie(cookie);
        response.addCookie(cookieRefresh);

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl).queryParam("token", token).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
