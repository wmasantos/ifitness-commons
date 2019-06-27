package br.com.ifitness.commons.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private PasswordEncoder encoder;

    @Autowired
    public SecurityConfiguration(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .passwordEncoder(new BCryptPasswordEncoder())
            .withUser("ifitness-web-user")
            .password(encoder.encode("iFWebUser"))
            .roles("USER")
            .and()
            .withUser("ifitness-mobile-user")
            .password(encoder.encode("iFMobileUser"))
            .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(
                "/v1/user/signIn",
                "/v1/user/signUp",
                "/v1/user/*/recovery",
                "/v1/user/*/changePassword",
                "/v2/api-docs",
                "/configuration/**",
                "/swagger*/**",
                "/webjars/**",
                "/",
                "/csrf")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf()
            .disable();
    }
}
