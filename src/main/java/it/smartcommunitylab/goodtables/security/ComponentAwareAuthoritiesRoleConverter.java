package it.smartcommunitylab.goodtables.security;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class ComponentAwareAuthoritiesRoleConverter extends NamespaceAwareAuthoritiesRoleConverter {
    private final static Logger _log = LoggerFactory.getLogger(ComponentAwareAuthoritiesRoleConverter.class);

    private final String component;

    private final static String PREFIX = "components/";

    public ComponentAwareAuthoritiesRoleConverter(String component) {
        super();
        this.component = component;
        _log.debug("create for component " + component);
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        // fetch from parent
        Collection<GrantedAuthority> authorities = super.convert(jwt);

        // filter if defined
        if (StringUtils.isBlank(component)) {
            return authorities;
        } else {
            // keep only those matching and cleanup prefix
            Collection<GrantedAuthority> componentAuthorities = new ArrayList<>();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().startsWith(PREFIX + component)) {
                    if (authority instanceof NamespacedGrantedAuthority) {
                        NamespacedGrantedAuthority a = (NamespacedGrantedAuthority) authority;
                        _log.trace(a.toString());

                        String s = StringUtils.removeStart(a.getSpace(), PREFIX + component);
                        if (s.startsWith("/")) {
                            // cleanup divider
                            s = s.substring(1);
                        }
                        // filter non-namespaced at component level
                        // e.g components/<component>:ROLE_PROVIDER agains
                        // components/<component>/<space>:ROLE_USER
                        if (StringUtils.isNotBlank(s)) {
                            componentAuthorities.add(new NamespacedGrantedAuthority(s, a.getRole()));
                        } else {
                            // consider as top authority
                            componentAuthorities.add(new SimpleGrantedAuthority(a.getRole()));
                        }
                    } else {
                        String a = StringUtils.removeStart(authority.getAuthority(), PREFIX + component);
                        componentAuthorities.add(new SimpleGrantedAuthority(a));
                    }
                }
            }

            return componentAuthorities;
        }

    }

}
