package edu.kcg.web3.finalproject.mapper

import edu.kcg.web3.finalproject.entity.Type

class TypeMapper(type: Type) {
    var id = type.id
    var typeName = type.typeName
}

class TypePlaceMapper(type: Type) {
    var id = type.id
    var typeName = type.typeName
    var placeList = type.placeList.map { PlaceMapper(it) }
}

class TypeUnsafe {
    var id: Long? = null
    var typeName: String? = null
}