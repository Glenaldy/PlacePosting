package edu.kcg.web3.finalproject.mapper

import edu.kcg.web3.finalproject.entity.User

class UserMapper(user: User) {
    var id = user.id
    var name = user.name
    var postedPlace = user.postedPlace.map { PlaceMapper(it) }
}