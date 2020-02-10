package it.smartcommunitylab.goodtables.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import it.smartcommunitylab.goodtables.security.AudienceValidator;
import it.smartcommunitylab.goodtables.security.ComponentAwareAuthoritiesRoleConverter;
import it.smartcommunitylab.goodtables.security.ComponentSecurityFilter;
import it.smartcommunitylab.goodtables.security.JwtAuthenticationConverter;
import it.smartcommunitylab.goodtables.security.ScopeAuthoritiesConverter;
import it.smartcommunitylab.goodtables.security.SimpleUserAuthoritiesConverter;

@Configuration
@EnableWebSecurity
public class ResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${auth.enabled}")
    private boolean authenticate;

    @Value("${auth.component}")
    private String component;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.client-id}")
    private String clientId;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (authenticate) {
            http
                    .authorizeRequests()
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/**").authenticated()
                    .antMatchers("/h2-console/**").permitAll()
                    .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(jwtTokenConverter());

        } else {
            http.authorizeRequests().anyRequest().permitAll();
        }

        // enable X-Frame options for console
        http.headers().frameOptions().sameOrigin();

        // add custom filter
        http.addFilterAfter(
                new ComponentSecurityFilter(component), BasicAuthenticationFilter.class);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(clientId);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    Converter<Jwt, AbstractAuthenticationToken> jwtTokenConverter() {
        return new JwtAuthenticationConverter(
                new ComponentAwareAuthoritiesRoleConverter(component),
                new ScopeAuthoritiesConverter());
        // example: assign any user a default role
//        return new JwtAuthenticationConverter(
//                new ComponentAwareAuthoritiesRoleConverter(component),
//                new ScopeAuthoritiesConverter(),
//                new SimpleUserAuthoritiesConverter());
    }
}
