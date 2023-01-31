package edu.kcg.web3.finalproject.repository

import edu.kcg.web3.finalproject.entity.Type
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TypeRepository : JpaRepository<Type, Long?> {
    @Query("SELECT t FROM type t WHERE t.typeName LIKE :name")
    fun findByTypeNameOrNull(name: String): Type?
}