package edu.kcg.web3.finalproject.repository

import edu.kcg.web3.finalproject.entity.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : JpaRepository<Place, Long?>