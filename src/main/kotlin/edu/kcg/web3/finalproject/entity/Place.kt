package edu.kcg.web3.finalproject.entity

import javax.persistence.*

@Entity
@Table(name = "place")
class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_place")
    val id: Long? = null

    @Column(name = "place_name", nullable = false)
    var name: String = ""

    @Column(name = "place_description")
    var description: String = ""

    @Column(name = "place_address")
    var address: String = ""

    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
            name = "type_list",
            joinColumns = [JoinColumn(name = "id_place")],
            inverseJoinColumns = [JoinColumn(name = "id_type")]
    )
    var typeList: MutableList<Type> = mutableListOf()

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_systemUser")
    var poster: User = User()
}
