package edu.kcg.web3.finalproject.entity

import javax.persistence.*

@Entity(name = "systemUser")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_systemUser")
    val id: Long? = null

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "email", nullable = false, unique = true)
    var email: String = ""

    @Column(name = "password", nullable = false)
    var password: String = ""

    @Column(name = "admin", nullable = false)
    var admin: Boolean = false

    @OneToMany(mappedBy = "poster", cascade = [CascadeType.ALL])
    val postedPlace: MutableList<Place> = mutableListOf()
}