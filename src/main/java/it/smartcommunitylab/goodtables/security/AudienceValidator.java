package it.smartcommunitylab.goodtables.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final static Logger _log = LoggerFactory.getLogger(AudienceValidator.class);

    public final OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

    private final String aud;

    public AudienceValidator(String audience) {
        super();
        this.aud = audience;
        _log.debug("create with audience " + aud);
    }

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience().contains(aud)) {
            return OAuth2TokenValidatorResult.success();
        } else {
            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}