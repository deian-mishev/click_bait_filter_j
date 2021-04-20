package com.clickbait.plugin.security;

import com.clickbait.plugin.services.ApplicationUserService;
import com.google.common.annotations.Beta;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static com.clickbait.plugin.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
public class CustomWebSecurity extends WebSecurityConfigurerAdapter {

    @Value("${spring.profiles.active}")
    private String activeSpringProfile;

    @Value("${authentication}")
    private Boolean shouldAuthenticate;

    @Value("${api.endpoints.authentication}")
    private String authenticationEndpoint;

    @Value("${encryption.passwordEncoder.salt}")
    private String apiSalt;

    @Value("${api.endpoints.processing}")
    private String processing;

    @Autowired
    private EncryptionHandlers encryptionHandlers;

    @Autowired
    private ApplicationUserService applicationUserService;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        Boolean isProd = activeSpringProfile.equals("prod");
        http.httpBasic().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        if (isProd || shouldAuthenticate) {
            http.addFilterBefore(new AuthenticationFilter(authenticationManager(), applicationUserService,
                    encryptionHandlers, authenticationEndpoint, apiSalt), UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(
                            new AuthenticateJwtFilter(applicationUserService, encryptionHandlers, processing, apiSalt),
                            UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/**").hasAnyRole(USER.name(), ADMIN.name())
                    .antMatchers(HttpMethod.GET, "/**").denyAll()
                    .antMatchers(HttpMethod.DELETE, "/**").denyAll()
                    .antMatchers(HttpMethod.HEAD, "/**").denyAll()
                    .antMatchers(HttpMethod.OPTIONS, "/**").denyAll()
                    .antMatchers(HttpMethod.PATCH, "/**").denyAll()
                    .antMatchers(HttpMethod.PUT, "/**").denyAll()
                    .antMatchers(HttpMethod.TRACE, "/**").denyAll()
                    .anyRequest().authenticated();
        } else {
            http.authorizeRequests().anyRequest().permitAll();
        }

        if (isProd) {
            http.cors().and().csrf().disable();
        } else {
            http.csrf().disable().cors().disable();
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Beta
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(applicationUserService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return encryptionHandlers.getMacPasswordEncoder();
    }
}
