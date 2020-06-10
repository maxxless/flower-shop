package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Flower.
 */
@Entity
@Table(name = "flower")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "flower")
data class Flower(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @get: NotNull
    @Column(name = "price", nullable = false)
    var price: Double? = null,

    @Lob
    @Column(name = "image")
    var image: ByteArray? = null,

    @Column(name = "image_content_type")
    var imageContentType: String? = null,

    @ManyToMany
    @JoinTable(name = "flower_available_colours",
        joinColumns = [JoinColumn(name = "flower_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "available_colours_id", referencedColumnName = "id")])
    var availableColours: MutableSet<Colour> = mutableSetOf(),

    @ManyToMany(mappedBy = "flowers")
    @JsonIgnore
    var collectionsIns: MutableSet<Collection> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addAvailableColours(colour: Colour): Flower {
        this.availableColours.add(colour)
        return this
    }

    fun removeAvailableColours(colour: Colour): Flower {
        this.availableColours.remove(colour)
        return this
    }

    fun addCollectionsIn(collection: Collection): Flower {
        this.collectionsIns.add(collection)
        collection.flowers.add(this)
        return this
    }

    fun removeCollectionsIn(collection: Collection): Flower {
        this.collectionsIns.remove(collection)
        collection.flowers.remove(this)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Flower) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Flower{" +
        "id=$id" +
        ", name='$name'" +
        ", description='$description'" +
        ", price=$price" +
        ", image='$image'" +
        ", imageContentType='$imageContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
