package it.smartcommunitylab.goodtables.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class ComponentSecurityFilter extends GenericFilterBean {

    private final static Logger _log = LoggerFactory.getLogger(SpacePermissionEvaluator.class);

    private final String component;

    private final static String PREFIX = "components/";

    public ComponentSecurityFilter(String component) {
        super();
        this.component = component;
    }

    @Override
    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        _log.trace("called");
        try {
            if (isAuthenticated() && StringUtils.isNotBlank(component)) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                logger.trace("found name " + authentication.getName());
                logger.trace("found principal " + authentication.getPrincipal().toString());
                logger.trace("found authorities " + authorities.toString());

                // filter, keep only those matching
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        chain.doFilter(request, response);
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }
}
