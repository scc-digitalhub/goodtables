package it.smartcommunitylab.goodtables.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.SystemKeys;

@Component
public class ScopePermissionEvaluator implements PermissionEvaluator {
    private final static Logger _log = LoggerFactory.getLogger(ScopePermissionEvaluator.class);

    @Value("${scopes.enabled}")
    private boolean enabled;

    @Value("${scopes.list}")
    private List<String> scopes;

    @Value("${scopes.default}")
    private String defaultScope;

    @Value("${scopes.roles.mapping.user}")
    private String roleUserMapping;

    @PostConstruct
    public void init() {
        _log.debug("scopePermission enabled? " + enabled);

        if (scopes == null) {
            scopes = new ArrayList<>();
        }

        // add placeholder to scopes if empty
        if (scopes.isEmpty()) {
            scopes.add("*");
        }

        // always add default scope if defined
        if (!defaultScope.isEmpty() && !scopes.contains(defaultScope)) {
            scopes.add(defaultScope);
        }

        _log.debug("scopes: " + scopes.toString());

        // set default mappings

        if (roleUserMapping.isEmpty()) {
            roleUserMapping = SystemKeys.ROLE_USER;
        }

        _log.debug("role mapping " + SystemKeys.ROLE_USER + " to " + roleUserMapping);

    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // no scope object to check
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {

        String scopeId = targetId.toString();
        String userId = authentication.getName();
        String action = permission.toString();

        for (GrantedAuthority ga : authentication.getAuthorities()) {
            _log.trace("user " + userId + " authority " + ga.toString());
        }

        boolean isPermitted = isScopePermitted(scopeId);
        _log.trace("user " + userId + " hasPermission scope " + scopeId + " permitted " + isPermitted);

        // check in Auth
        boolean hasPermission = false;

        // fetch ONLY scope roles
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        _log.trace("user " + userId + " authorities " + authorities.toString());

        Set<String> roles = new HashSet<>();
        roles.addAll(getScopeRoles(scopeId, authorities));
        
        _log.trace("user " + userId + " roles " + roles.toString());


        // user role is enough for all operations
        hasPermission = roles.contains(SystemKeys.ROLE_USER);

        _log.debug("user " + userId + " hasPermission for scope " + scopeId + ":" + action + " " + hasPermission);

        return (isPermitted && hasPermission);
    }

    /*
     * Helpers
     */
    public List<String> getScopeRoles(String scopeId, Authentication authentication) {
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        return getScopeRoles(scopeId, authorities);
    }

    public boolean isScopePermitted(String scopeId) {

        if (!defaultScope.isEmpty() && scopeId.equals(defaultScope)) {
            // default scope always enabled if defined
            return true;
        }

        if (enabled) {
            if (scopes.contains("*")) {
                return true;
            }
            return scopes.contains(scopeId);
        }

        return false;
    }

    private List<String> getScopeRoles(String scopeId, List<GrantedAuthority> authorities) {
        List<String> roles = new ArrayList<>();

        for (GrantedAuthority ga : authorities) {
            // support variable substitution with placeholder <scope>
            String auth = ga.getAuthority();
            if (auth != null) {
                // check against mappings
                if (auth.equals(roleUserMapping.replace("<scope>", scopeId))) {
                    roles.add(SystemKeys.ROLE_USER);
                }

            }
        }

        return roles;
    }

}
