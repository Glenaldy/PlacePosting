package edu.kcg.web3.finalproject.service

import edu.kcg.web3.finalproject.config.CustomAuthority
import edu.kcg.web3.finalproject.entity.Place
import edu.kcg.web3.finalproject.entity.User
import edu.kcg.web3.finalproject.mapper.PlaceMapper
import edu.kcg.web3.finalproject.mapper.PlaceUnsafe
import edu.kcg.web3.finalproject.repository.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class PlaceService(
        @Autowired val placeRepository: PlaceRepository,
        @Autowired val typeService: TypeService,
        @Autowired val userService: UserService
) {
    fun fetchAll(): List<Place> {
        return placeRepository.findAll()
    }

    fun fetchPlaceByPoster(user: User): List<Place> {
        user.let { return it.postedPlace }
    }

    fun fetchPlaceByIdOrNull(id: Long?): Place? {
        id?.let {
            return placeRepository.findByIdOrNull(id)
        } ?: return null
    }

    fun sanitizePlace(placeUnsafe: PlaceUnsafe, newPlace: Place): Place {
        return newPlace.also { place ->
            place.name = placeUnsafe.name ?: place.name
            place.description = placeUnsafe.description ?: place.description
            place.address = placeUnsafe.address ?: place.description
            placeUnsafe.typeList?.let {
                place.typeList.clear()
                it.forEach { id ->
                    id?.run {
                        typeService.fetchTypeByIdOrNull(id)?.let { type ->
                            place.typeList.add(type)
                        }
                    }
                }
            }
        }
    }

    fun insertPlaceUnsafeOrNull(placeUnsafe: PlaceUnsafe, authentication: Authentication?): Place? {
        authentication?.let {
            userService.fetchUserByAuthenticationOrNull(authentication)?.let { poster ->
                return insertObject(sanitizePlace(placeUnsafe, Place().also { it.poster = poster }))
            } ?: return null
        } ?: return null
    }

    fun insertObject(place: Place): Place {
        return placeRepository.save(place)
    }

    fun updatePlaceUnsafeOrNull(placeUnsafe: PlaceUnsafe, authentication: Authentication?): Place? {
        authentication?.let {
            // check if the object in db
            fetchPlaceByIdOrNull(placeUnsafe.id)?.let { place ->
                // check if authorized
                userService.fetchUserByAuthenticationOrNull(authentication)?.let { user ->
                    if (place.poster == user || user.admin) {
                        return updatePlace(sanitizePlace(placeUnsafe, place))
                    } else return null
                }
            } ?: return null
        } ?: return null
    }

    fun updatePlace(place: Place): Place? {
        return placeRepository.save(place)
    }

    fun deletePlaceByIdOrNull(id: Long?, authentication: Authentication?): PlaceMapper? {
        authentication?.let {
            fetchPlaceByIdOrNull(id)?.let { place ->
                userService.fetchUserByAuthenticationOrNull(authentication)?.let { user ->
                    if (place.poster == user || user.admin) {
                        return deletePlace(place)
                    } else return null
                }
            } ?: return null
        } ?: return null
    }


    fun deletePlace(place: Place): PlaceMapper {
        val returnPlace = PlaceMapper(place)
        placeRepository.delete(place)
        return returnPlace
    }

    fun placeEditAuthorization(place: Place, user: User): Boolean {
        if (userService.fetchUserAuthority(user) == CustomAuthority.ADMIN.authority
                || place.poster == user) {
            return true
        }
        return false
    }

}