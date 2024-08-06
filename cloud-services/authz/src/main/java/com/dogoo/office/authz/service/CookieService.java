package com.dogoo.office.authz.service;

import com.dogoo.office.authz.util.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CookieService {

    public ResponseCookie generateTokenCookie(String token) {
        return ResponseCookie.from(Constants.DOGOO_ACCESS_TOKEN, token).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();

    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(Constants.DOGOO_REFRESH_TOKEN, refreshToken).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    public ResponseCookie getCleanTokenCookie() {
        return ResponseCookie.from(Constants.DOGOO_ACCESS_TOKEN).path("/api").build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(Constants.DOGOO_REFRESH_TOKEN).path("/api").build();
    }

    public String getSecuredCookie(HttpServletRequest request, String cookieName) {
        Cookie defaultCookie = new Cookie(cookieName, "");

        defaultCookie.setSecure(true);
        defaultCookie.setHttpOnly(true);

        if (request.getCookies() == null) {
            return null;
        }

        return Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName) &&
                        cookie.getValue() != null && !cookie.getValue().isEmpty())
                .findFirst().orElse(defaultCookie).getValue();
    }
}
