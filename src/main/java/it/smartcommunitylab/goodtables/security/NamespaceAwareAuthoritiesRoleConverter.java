package it.smartcommunitylab.goodtables.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class NamespaceAwareAuthoritiesRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final static Logger _log = LoggerFactory.getLogger(NamespaceAwareAuthoritiesRoleConverter.class);

    private final static String[] WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = { "authorities", "roles" };

    private String authoritiesClaimName;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        _log.trace("extract authorities from " + jwt.getClaims().keySet().toString());

        Collection<GrantedAuthority> authorities = new LinkedList<>();

        for (String authority : getAuthorities(jwt)) {
            // evaluate if authority contains space
            // as "<context>/<space>:<role>
            if (authority.contains(":")) {
                Pair<String, String> s = spaceAwareExtractor(authority);
                authorities.add(new NamespacedGrantedAuthority(s.getFirst(), s.getSecond()));
            } else {
                authorities.add(new SimpleGrantedAuthority(authority));
            }

        }

        return authorities;
    }

    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private String getAuthoritiesClaimName(Jwt jwt) {

        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        }

        for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.containsClaim(claimName)) {
                return claimName;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getAuthoritiesClaimName(jwt);

        if (claimName == null) {
            return Collections.emptyList();
        }

        Object authorities = jwt.getClaim(claimName);
        if (authorities instanceof String) {
            if (StringUtils.hasText((String) authorities)) {
                return Arrays.asList(((String) authorities).split(" "));
            } else {
                return Collections.emptyList();
            }
        } else if (authorities instanceof Collection) {
            return (Collection<String>) authorities;
        }

        return Collections.emptyList();
    }

    private Pair<String, String> spaceAwareExtractor(String a) {
        String[] s = a.split(":");
        return Pair.of(s[0], s[1]);
    }
}
