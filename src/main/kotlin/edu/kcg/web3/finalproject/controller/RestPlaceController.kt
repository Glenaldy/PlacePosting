package edu.kcg.web3.finalproject.controller

import edu.kcg.web3.finalproject.mapper.PlaceMapper
import edu.kcg.web3.finalproject.mapper.PlaceUnsafe
import edu.kcg.web3.finalproject.service.PlaceService
import edu.kcg.web3.finalproject.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/place")
class RestPlaceController(
        @Autowired private val placeService: PlaceService,
        @Autowired private val userService: UserService,
) {
    @GetMapping
    fun getAll(): List<PlaceMapper> {
        return placeService.fetchAll().map { PlaceMapper(it) }
    }

    @GetMapping("/mine")
    fun getAllPlaceByPoster(authentication: Authentication): ResponseEntity<List<PlaceMapper>> {
        userService.fetchUserByAuthenticationOrNull(authentication)?.let { systemUser ->
            return ResponseEntity(placeService.fetchPlaceByPoster(systemUser).map { PlaceMapper(it) }, HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @GetMapping("/{id}")
    fun getPlaceById(@PathVariable id: Long?): ResponseEntity<PlaceMapper> {
        placeService.fetchPlaceByIdOrNull(id)?.let {
            return ResponseEntity(PlaceMapper(it), HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun insertNew(@RequestBody placeUnsafe: PlaceUnsafe, authentication: Authentication): ResponseEntity<PlaceMapper> {
        placeService.insertPlaceUnsafeOrNull(placeUnsafe, authentication)?.let {
            return ResponseEntity(PlaceMapper(it), HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@RequestBody place: PlaceUnsafe, authentication: Authentication): ResponseEntity<PlaceMapper> {
        placeService.updatePlaceUnsafeOrNull(place, authentication)?.let {
            return ResponseEntity(PlaceMapper(it), HttpStatus.OK)
        } ?: let { return ResponseEntity(HttpStatus.NOT_FOUND) }
    }


    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long?, authentication: Authentication): ResponseEntity<PlaceMapper> {
        // find the place
        val place = placeService.fetchPlaceByIdOrNull(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        // determine authorization
        val authorized = userService.fetchUserByAuthenticationOrNull(authentication)?.let {
            placeService.placeEditAuthorization(place, it)
        } ?: let { return ResponseEntity(HttpStatus.UNAUTHORIZED) }

        // delete
        return if (authorized) {
            ResponseEntity(placeService.deletePlace(place), HttpStatus.OK)
        } else ResponseEntity(HttpStatus.UNAUTHORIZED)
    }
}