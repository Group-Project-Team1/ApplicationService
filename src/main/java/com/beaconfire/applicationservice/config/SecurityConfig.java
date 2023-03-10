package com.beaconfire.applicationservice.config;

import com.beaconfire.applicationservice.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtFilter jwtFilter;

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/application-service/hr/**").hasAuthority("hr")
                .antMatchers("/application-service/employee/**").hasAuthority("employee")
                .antMatchers("/application-service/all/**").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/application-service/v2/api-docs/**");
        web.ignoring().antMatchers("/application-service/swagger.json");
        web.ignoring().antMatchers("/application-service/swagger-ui.html");
        web.ignoring().antMatchers("/application-service/swagger-resources/**");
        web.ignoring().antMatchers("/application-service/webjars/**");
    }
}


