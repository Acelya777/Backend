package com.example.backend.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${app.manager.dn}")
    String MANAGER_DN;

    @Value("${app.manager.password}")
    String MANAGER_PASSWORD;

    @Value("${app.ldap.dn}")
    String LDAP_DN;

    @Value("${app.ldap.search.base}")
    String LDAP_SEARCH_BASE;

    @Value("${app.ldap.root}")
    String LDAP_ROOT;

    @Value("${app.ldap.url.with.port}")
    String LDAP_URL_WITH_PORT;

    @Value("${app.search.filter}")
    String APP_SEARCH_FILTER;

    @Value("${app.group.search.filter}")
    String APP_GROUP_SEARCH_FILTER;
    @Value("${ldap.enabled}")
    private String ldapEnabled;
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(corsFilter(), SessionManagementFilter.class)
                .csrf().disable().authorizeRequests()
                .anyRequest().authenticated().and()  //için herhangi bir istekte de herkese açık erişim izni verilmiştir.
                .formLogin().loginPage("/login").permitAll() //giriş sayfası (/login) tanımlanmış ve bu sayfaya herkese açık erişim izni verilmiştir.
                .and().httpBasic();
    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if(Boolean.parseBoolean(ldapEnabled)){
            auth
                    .ldapAuthentication().userSearchBase(LDAP_SEARCH_BASE)
                    .userSearchFilter(APP_SEARCH_FILTER)
                    .groupSearchBase(LDAP_SEARCH_BASE)
                    .groupSearchFilter(APP_GROUP_SEARCH_FILTER).contextSource()
                    .root(LDAP_ROOT).url(LDAP_URL_WITH_PORT)
                    .managerDn(MANAGER_DN)
                    .managerPassword(MANAGER_PASSWORD);
        } else{
            auth.inMemoryAuthentication()
                    .withUser("user").password("password");
        }
    }
}
