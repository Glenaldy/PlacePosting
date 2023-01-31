package edu.kcg.web3.finalproject.entity

import javax.persistence.*

@Entity(name = "type")
class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type")
    val id: Long? = null

    @Column(name = "type_name", nullable = false, unique = true)
    var typeName: String = ""

    @ManyToMany(mappedBy = "typeList")
    var placeList: MutableSet<Place> = mutableSetOf()
}