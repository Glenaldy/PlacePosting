package edu.kcg.web3.finalproject.mapper

import edu.kcg.web3.finalproject.entity.Place

class PlaceMapper(place: Place) {
    var id = place.id ?: -1
    var name = place.name
    var description = place.description
    var address = place.address
    var typeList = place.typeList.map { it.id }
    var poster = place.poster.id
}

class PlaceUnsafe {
    var id: Long? = null
    var name: String? = null
    var description: String? = null
    var address: String? = null
    var typeList: List<Long?>? = null
}