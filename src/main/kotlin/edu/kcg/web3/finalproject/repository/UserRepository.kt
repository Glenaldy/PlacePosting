package edu.kcg.web3.finalproject.repository

import edu.kcg.web3.finalproject.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long?>{
    @Query("SELECT u FROM systemUser u WHERE u.email LIKE :email")
    fun findByEmail(email: String): User?
}