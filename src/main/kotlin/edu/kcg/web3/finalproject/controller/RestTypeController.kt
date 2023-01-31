package edu.kcg.web3.finalproject.controller

import edu.kcg.web3.finalproject.mapper.TypeMapper
import edu.kcg.web3.finalproject.mapper.TypePlaceMapper
import edu.kcg.web3.finalproject.mapper.TypeUnsafe
import edu.kcg.web3.finalproject.service.TypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/place-type")
class RestTypeController(
        @Autowired private val typeService: TypeService
) {
    @GetMapping
    fun getAll(): List<TypeMapper> {
        return typeService.fetchAll().map { TypeMapper(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long?): ResponseEntity<TypePlaceMapper> {
        typeService.fetchTypeByIdOrNull(id)?.let {
            return ResponseEntity(TypePlaceMapper(it), HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    }


    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun insert(@RequestBody typeUnsafe: TypeUnsafe): ResponseEntity<TypeMapper> {
        typeService.insertTypeUnsafeOrNull(typeUnsafe)?.let {
            return ResponseEntity(TypeMapper(it), HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
    }


    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long?): ResponseEntity<TypeMapper> {
        typeService.fetchTypeByIdOrNull(id)?.let {
            return ResponseEntity(typeService.deleteType(it), HttpStatus.OK)
        } ?: let { return ResponseEntity(HttpStatus.NOT_FOUND) }
    }
}