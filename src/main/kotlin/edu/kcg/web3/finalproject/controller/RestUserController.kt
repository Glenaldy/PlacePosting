package edu.kcg.web3.finalproject.controller

import edu.kcg.web3.finalproject.mapper.UserMapper
import edu.kcg.web3.finalproject.repository.UserRepository
import edu.kcg.web3.finalproject.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class RestUserController(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val userService: UserService
) {
    @GetMapping("/all")
    fun getAll(): List<UserMapper> {
        return userRepository.findAll().map { UserMapper(it) }
    }

    @GetMapping
    fun getCurrentUser(authentication: Authentication): ResponseEntity<UserMapper> {
        userService.fetchUserByAuthenticationOrNull(authentication)?.let {
            return ResponseEntity(UserMapper(it), HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    }
}