package com.dogoo.office.authz.service;

import com.dogoo.office.authz.exception.InvalidTokenException;
import com.dogoo.office.authz.security.model.UserTokenModel;
import com.dogoo.office.authz.util.CharKeys;
import com.dogoo.office.authz.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Optional;

@Service
public class TokenService {

    private final CookieService cookieService;

    public TokenService(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    public String getTokenByName(HttpServletRequest request, String tokenName) {
        String token = cookieService.getSecuredCookie(request, tokenName);

        if (token == null || token.isEmpty()) {
            token = request.getHeader(tokenName);
        }

        return token;
    }

    public Optional<String> parseJwt(HttpServletRequest request) {

        String token = cookieService.getSecuredCookie(request, Constants.DOGOO_ACCESS_TOKEN);

        if (token == null || token.isEmpty()) {
            token = request.getHeader(Constants.DOGOO_AUTH_HEADER);
        }

        if (StringUtils.hasText(token)) {
            return Optional.of(token);
        }

        return Optional.empty();
    }

    public String getTokenPayload(String token) {

        if (token == null || token.isEmpty()) throw new InvalidTokenException("TOKEN", "INVALID");

        String [] tokenArray = StringUtils.tokenizeToStringArray(token, CharKeys.DOT);

        String encodedTokenPayload = tokenArray.length == 3 ? tokenArray[1] : Constants.EMPTY_STRING;

        return new String(Base64.getDecoder().decode(encodedTokenPayload));
    }

    public UserTokenModel mapJwtToUserModel(String payload) throws JsonProcessingException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ObjectMapper om = new ObjectMapper();

        return om.readValue(payload, UserTokenModel.class);

    }
}
