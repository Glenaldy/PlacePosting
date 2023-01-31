package edu.kcg.web3.finalproject.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig {

    private val logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Autowired
    private val authProvider: CustomAuthenticationProvider? = null

    @Autowired
    fun configAuthentication(auth: AuthenticationManagerBuilder) {
        logger.info("Registering AuthenticationProvider")
        auth.authenticationProvider(authProvider)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        logger.info("Configuring Spring security")
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .and()
                .authorizeRequests()

                // REST
                .antMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority(CustomAuthority.ADMIN.authority, CustomAuthority.USER.authority)
                .antMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority(CustomAuthority.ADMIN.authority, CustomAuthority.USER.authority)
                .antMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority(CustomAuthority.ADMIN.authority, CustomAuthority.USER.authority)
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority(CustomAuthority.ADMIN.authority, CustomAuthority.USER.authority)

                // VIEW
                .antMatchers(HttpMethod.GET, "/admin/**").hasAuthority(CustomAuthority.ADMIN.authority)
                .antMatchers(HttpMethod.POST, "/admin/**").hasAuthority(CustomAuthority.ADMIN.authority)

                .antMatchers(HttpMethod.GET, "/register").permitAll()
                .antMatchers(HttpMethod.GET, "/**").hasAnyAuthority(CustomAuthority.ADMIN.authority, CustomAuthority.USER.authority)

                // Login Page
                .and().httpBasic()
                .and().formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")


                // Log out
                .and()
                .logout().logoutUrl("/logout")
                .invalidateHttpSession(true)

                .and().cors()
                .and().csrf().disable()
        return http.build()
    }
}



