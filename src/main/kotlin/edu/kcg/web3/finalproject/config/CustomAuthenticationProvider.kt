package edu.kcg.web3.finalproject.config

import edu.kcg.web3.finalproject.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider(
        @Autowired private val passwordEncoder: PasswordEncoder,
        @Autowired private val userService: UserService
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {

        val email = authentication.name
        val password = authentication.credentials.toString()
        val customer = userService.fetchByEmailOrNull(email)
                ?: throw BadCredentialsException("Bad credentials. Please try again.")

        if (customer.password.isNotEmpty() && passwordEncoder.matches(password, customer.password)) {
            val authorities = mutableListOf<SimpleGrantedAuthority>()
            if (customer.admin) {
                authorities.add(SimpleGrantedAuthority(CustomAuthority.ADMIN.authority))
            } else authorities.add(SimpleGrantedAuthority(CustomAuthority.USER.authority))

            return UsernamePasswordAuthenticationToken(email, password, authorities)
        } else {
            throw BadCredentialsException("Bad credentials. Please try again.")
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

}