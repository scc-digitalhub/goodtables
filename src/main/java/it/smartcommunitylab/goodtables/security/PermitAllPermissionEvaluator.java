package it.smartcommunitylab.goodtables.security;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class PermitAllPermissionEvaluator implements PermissionEvaluator {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * @return true always
     */
    public boolean hasPermission(Authentication authentication, Object target,
            Object permission) {
        logger.warn("Allowing user " + authentication.getName() + " permission '"
                + permission + "' on object " + target);
        return true;
    }

    /**
     * @return true always
     */
    public boolean hasPermission(Authentication authentication, Serializable targetId,
            String targetType, Object permission) {
        logger.warn("Allowing user " + authentication.getName() + " permission '"
                + permission + "' on object with Id '" + targetId);
        return true;
    }
}