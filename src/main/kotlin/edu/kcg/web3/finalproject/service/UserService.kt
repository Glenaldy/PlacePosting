package edu.kcg.web3.finalproject.service

import edu.kcg.web3.finalproject.config.CustomAuthority
import edu.kcg.web3.finalproject.entity.User
import edu.kcg.web3.finalproject.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class UserService(
        @Autowired private val userRepository: UserRepository
) {
    fun fetchUserByAuthenticationOrNull(authentication: Authentication?): User? {
        authentication?.let {
            if (authentication !is AnonymousAuthenticationToken) {
                return userRepository.findByEmail(authentication.principal.toString())
            }
            return null
        } ?: return null
    }

    fun fetchByEmailOrNull(email: String?): User? {
        email?.let {
            userRepository.findByEmail(email)?.let { user ->
                return user
            } ?: return null
        } ?: return null
    }

    fun fetchUserAuthority(user: User): String {
        return if (user.admin) CustomAuthority.ADMIN.authority
        else CustomAuthority.USER.authority
    }

    fun insertUser(user: User): User {
        return userRepository.save(user)
    }
}