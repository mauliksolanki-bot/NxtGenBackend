package com.nxtgen.api.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.security.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import static com.nxtgen.api.constant.NxtGenCommonConstant.ENABLED_FLAG;
import static com.nxtgen.api.constant.NxtGenCommonConstant.LOCKED_FLAG;

/**
 * Resolves the signed-in user from the Authorization header. Controllers in this
 * code base read the bearer token themselves because there is no global filter.
 */
@Component
public class AuthenticatedUserResolver {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_MESSAGE = "Your session could not be verified.";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMasterRepository userMasterRepository;

    public AuthenticatedUserResolver(
            JwtTokenProvider jwtTokenProvider,
            UserMasterRepository userMasterRepository
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMasterRepository = userMasterRepository;
    }

    public UserMasterEntity resolve(String authorizationHeader) {
        String token = Optional.ofNullable(authorizationHeader)
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()).trim())
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_TOKEN_MESSAGE));

        Claims claims;
        try {
            claims = jwtTokenProvider.parseClaims(token);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthenticationFailedException(INVALID_TOKEN_MESSAGE);
        }

        boolean expired = claims.getExpiration() == null || claims.getExpiration().before(new Date());
        if (expired) {
            throw new AuthenticationFailedException(INVALID_TOKEN_MESSAGE);
        }

        return userMasterRepository.findByUsername(claims.getSubject())
                .filter(user -> ENABLED_FLAG.equalsIgnoreCase(user.getActvFlag()))
                .filter(user -> !LOCKED_FLAG.equalsIgnoreCase(user.getIsLocked()))
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_TOKEN_MESSAGE));
    }
}
