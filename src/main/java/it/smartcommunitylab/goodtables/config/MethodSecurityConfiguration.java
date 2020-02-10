package it.smartcommunitylab.goodtables.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import it.smartcommunitylab.goodtables.security.PermitAllPermissionEvaluator;
import it.smartcommunitylab.goodtables.security.SpacePermissionEvaluator;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Value("${spaces.enabled}")
    private boolean enabled;

    @Value("${spaces.list}")
    private List<String> spaces;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
        methodSecurityExpressionHandler.setPermissionEvaluator(permissionEvaluator());
        return methodSecurityExpressionHandler;
    }

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        if (enabled) {
            return new SpacePermissionEvaluator(spaces);
        } else {
            return new PermitAllPermissionEvaluator();
        }
    }
}
