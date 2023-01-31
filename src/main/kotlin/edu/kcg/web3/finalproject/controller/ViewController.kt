package edu.kcg.web3.finalproject.controller

import edu.kcg.web3.finalproject.config.CustomAuthority
import edu.kcg.web3.finalproject.mapper.PlaceUnsafe
import edu.kcg.web3.finalproject.mapper.TypeUnsafe
import edu.kcg.web3.finalproject.service.PlaceService
import edu.kcg.web3.finalproject.service.TypeService
import edu.kcg.web3.finalproject.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping("/")
class ViewController(
        @Autowired private val userService: UserService,
        @Autowired private val typeService: TypeService,
        @Autowired private val placeService: PlaceService,
        @Autowired private val loginController: LoginController
) {

    @ExceptionHandler(EmptyResultDataAccessException::class)
    @RequestMapping("/error_page")
    fun error(model: Model, authentication: Authentication?): String {
        model["title"] = "Error"
        userService.fetchUserByAuthenticationOrNull(authentication)?.let {
            model["user"] = it
            return index(model, authentication)
        } ?: let {
            model["message"] = "Error, please login"
            return loginController.login(model)
        }
    }

    @RequestMapping
    fun index(model: Model, authentication: Authentication?): String {
        // Redirect to all place
        userService.fetchUserByAuthenticationOrNull(authentication)?.let {
            return allPlaces(model, authentication)
        } ?: return loginController.login(model)
    }

    @RequestMapping("/show_all")
    fun allPlaces(model: Model, authentication: Authentication?): String {
        authentication?.let {
            model["title"] = "All Places"
            model["user"] = userService.fetchUserByAuthenticationOrNull(authentication)!!
            model["place"] = placeService.fetchAll()
            return "all_places"
        } ?: return error(model, null)

    }

    @RequestMapping("/show_mine")
    fun myPlaces(model: Model, authentication: Authentication?): String {
        authentication?.let {
            model["title"] = "My Places"
            val loggedUser = userService.fetchUserByAuthenticationOrNull(authentication)!!
            model["user"] = loggedUser
            model["place"] = placeService.fetchPlaceByPoster(loggedUser)
            return "my_places"
        } ?: return error(model, null)
    }

    @RequestMapping("/insert_place")
    fun insertPlace(model: Model, authentication: Authentication?): String {
        authentication?.let {
            model["title"] = "Insert New Place"
            model["user"] = userService.fetchUserByAuthenticationOrNull(authentication)!!
            model["allType"] = typeService.fetchAll()
            return "insert_place"
        } ?: return error(model, null)
    }

    @PostMapping("/insert_place")
    fun processInsertPlace(
            model: Model,
            @RequestParam name: String?,
            @RequestParam description: String?,
            @RequestParam address: String?,
            @RequestParam type: Long?,
            authentication: Authentication?
    ): String {
        val place = PlaceUnsafe().also {
            it.name = name
            it.description = description
            it.address = address
            it.typeList = listOf(type)
        }
        placeService.insertPlaceUnsafeOrNull(place, authentication)?.let {
            return myPlaces(model, authentication)
        }
        return myPlaces(model, authentication)
    }

    @RequestMapping("/edit_place/{id}")
    fun editPlace(
        model: Model,
        authentication: Authentication?,
        @PathVariable id: Long
    ): String {
        authentication?.let {
            model["title"] = "Edit Place"
            model["user"] = userService.fetchUserByAuthenticationOrNull(authentication)!!
            val place = placeService.fetchPlaceByIdOrNull(id) ?: let {
                model["message"] = "Place not found."
                return error(model, authentication)
            }

            val authorized = userService.fetchUserByAuthenticationOrNull(authentication)?.let {
                placeService.placeEditAuthorization(place, it)
            } ?: let {
                model["message"] = "Not authorized"
                return error(model, authentication)
            }

            model["allType"] = typeService.fetchAll()
            model["place"] = place
            if (authorized) return "edit_place"
            model["message"] = "Not authorized"
            return error(model, authentication)
        } ?: return error(model, null)
    }

    @PostMapping("/edit_place")
    fun processEditPlace(
            model: Model,
            @RequestParam id: Long?,
            @RequestParam name: String?,
            @RequestParam description: String?,
            @RequestParam address: String?,
            @RequestParam type: Long?,
            authentication: Authentication?
    ): String {
        model["title"] = "All Places"
        model["user"] = userService.fetchUserByAuthenticationOrNull(authentication)!!
        val place = PlaceUnsafe().also {
            it.id = id ?: it.id
            it.name = name ?: it.name
            it.description = description ?: it.description
            it.address = address ?: it.address
            it.typeList = listOf(type)
        }
        placeService.updatePlaceUnsafeOrNull(place, authentication)?.let {
            return myPlaces(model, authentication)
        }
        return myPlaces(model, authentication)
    }


    @PostMapping("/delete_place")
    fun deletePlace(model: Model, @RequestParam delete: Long?, authentication: Authentication?): String {
        placeService.deletePlaceByIdOrNull(delete, authentication)?.let {
            return myPlaces(model, authentication)
        } ?: let {
            model["message"] = "Place not found."
            return error(model, authentication)
        }
    }


    // Admin only menu
    @RequestMapping("/admin/types")
    fun allTypes(model: Model, authentication: Authentication?): String {
        authentication?.let {
            model["title"] = "All Types"
            model["type"] = typeService.fetchAll()
            return "all_types"
        } ?: return error(model, null)
    }

    @PostMapping("/admin/delete_type")
    fun deleteType(model: Model, authentication: Authentication?, @RequestParam delete: Long?): String {
        userService.fetchUserByAuthenticationOrNull(authentication)?.let { admin ->
            if (userService.fetchUserAuthority(admin) == CustomAuthority.ADMIN.authority) {
                typeService.fetchTypeByIdOrNull(delete)?.let {
                    typeService.deleteType(it)
                }
            }
        }
        return allTypes(model, authentication)
    }

    @PostMapping("/admin/insert_type")
    fun insertType(model: Model, authentication: Authentication?, @RequestParam typeName: String?): String {
        userService.fetchUserByAuthenticationOrNull(authentication)?.let { admin ->
            if (userService.fetchUserAuthority(admin) == CustomAuthority.ADMIN.authority) {
                typeService.insertTypeUnsafeOrNull(TypeUnsafe().also { it.typeName = typeName }) ?: let {
                    model["message"] = "Duplicate type name"
                    error(model, authentication)
                }
            } else return error(model, authentication)
        }
        return allTypes(model, authentication)
    }
}
