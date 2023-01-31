package edu.kcg.web3.finalproject.controller

import edu.kcg.web3.finalproject.config.CustomAuthority
import edu.kcg.web3.finalproject.entity.User
import edu.kcg.web3.finalproject.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LoginController(
        @Autowired private val userService: UserService,
        @Autowired private val passwordEncoder: PasswordEncoder
) {
    @GetMapping("/login")
    fun login(model: Model): String {
        model["title"] = "Login"
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        model["roles"] = listOf(mapOf("role" to CustomAuthority.ADMIN.authority),
                mapOf("role" to CustomAuthority.USER.authority)
        )
        model["title"] = "Register"
        return "register"
    }

    @PostMapping("/register")
    fun addUser(model: Model,
                @RequestParam name: String?,
                @RequestParam username: String?,
                @RequestParam password: String?,
                @RequestParam role: String?
    ): String {
        if (name.isNullOrBlank() ||
                username.isNullOrBlank() ||
                password.isNullOrBlank() ||
                role.isNullOrBlank()) {
            model["message"] = "Invalid"
            return register(model)
        }
        return try {
            val newUser = User().also {
                it.name = name
                it.email = username
                it.password = passwordEncoder.encode(password)
                it.admin = role == CustomAuthority.ADMIN.authority
            }
            userService.insertUser(newUser)
            model["message"] = "Registered, please login"
            login(model)
        } catch (e: Exception) {
            model["message"] = "Email/user already registered"
            register(model)
        }
    }
}