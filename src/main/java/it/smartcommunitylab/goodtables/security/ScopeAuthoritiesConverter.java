package it.smartcommunitylab.goodtables.security;

import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class ScopeAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final static Logger _log = LoggerFactory.getLogger(ScopeAuthoritiesConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        _log.trace("extract authorities from " + jwt.getClaims().keySet().toString());

        Collection<GrantedAuthority> authorities = new LinkedList<>();

        // extract from scopes
        // the spec says the scope is separated by spaces
        String[] scopes = jwt.getClaimAsString("scope").split("[\\s+]");
        for (String scope : scopes) {
            authorities.add(new ScopeGrantedAuthority(scope));
        }
        return authorities;
    }

}
