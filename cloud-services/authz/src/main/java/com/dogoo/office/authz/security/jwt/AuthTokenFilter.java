package com.dogoo.office.authz.security.jwt;

import com.dogoo.office.authz.security.model.UserDetailsImpl;
import com.dogoo.office.authz.security.model.UserDogoo;
import com.dogoo.office.authz.security.model.UserTokenModel;
import com.dogoo.office.authz.security.services.UserDetailsServiceImpl;
import com.dogoo.office.authz.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public AuthTokenFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {

           parseJwt(request).flatMap(tokenProvider::validateTokenAndGetJws).ifPresent(jwt -> {

               String dogooContext =  request.getHeader(Constants.DOGOO_CONTEXT_HEADER);

               if (dogooContext == null || dogooContext.isEmpty()) {
                   dogooContext = Constants.EMPTY_STRING;
               }

               UserDetails userDetails;

               try {

                   UserTokenModel userTokenModel = mapJwtToUserModel(jwt.getPayload());

                   userDetails = UserDetailsImpl.build(userTokenModel, dogooContext);
               } catch (JsonProcessingException e) {
                   throw new RuntimeException(e);
               }

               UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authentication);
           });

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {0}", e);
        }

        filterChain.doFilter(request, response);

    }

    private UserTokenModel mapJwtToUserModel(Claims jwt) throws JsonProcessingException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ObjectMapper om = new ObjectMapper();

        String payload = ow.writeValueAsString(jwt);

        return om.readValue(payload, UserTokenModel.class);

    }

    private Optional<String> parseJwt(HttpServletRequest request) {

        String token = getAccessTokenByCookies(request);

        if (token == null || token.isEmpty()) {
            token = request.getHeader(Constants.DOGOO_AUTH_HEADER);
        }

        if (StringUtils.hasText(token)) {
            return Optional.of(token);
        }

        return Optional.empty();
    }

    public String getAccessTokenByCookies(HttpServletRequest request) {
        Cookie defaultCookie = new Cookie(Constants.DOGOO_ACCESS_TOKEN, "");

        defaultCookie.setSecure(true);
        defaultCookie.setHttpOnly(true);

        if (request.getCookies() == null) {
            return null;
        }

        return Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(Constants.DOGOO_ACCESS_TOKEN) &&
                        cookie.getValue() != null && !cookie.getValue().isEmpty())
                .findFirst().orElse(defaultCookie).getValue();
    }

}
