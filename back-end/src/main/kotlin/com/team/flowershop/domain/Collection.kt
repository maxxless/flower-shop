package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Collection.
 */
@Entity
@Table(name = "collection")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "collection")
data class Collection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
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
    @JoinTable(name = "collection_available_packings",
        joinColumns = [JoinColumn(name = "collection_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "available_packings_id", referencedColumnName = "id")])
    var availablePackings: MutableSet<Packing> = mutableSetOf(),

    @ManyToMany
    @JoinTable(name = "collection_flowers",
        joinColumns = [JoinColumn(name = "collection_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "flowers_id", referencedColumnName = "id")])
    var flowers: MutableSet<Flower> = mutableSetOf(),

    @ManyToOne @JsonIgnoreProperties("collections")
    var category: Category? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addAvailablePackings(packing: Packing): Collection {
        this.availablePackings.add(packing)
        return this
    }

    fun removeAvailablePackings(packing: Packing): Collection {
        this.availablePackings.remove(packing)
        return this
    }

    fun addFlowers(flower: Flower): Collection {
        this.flowers.add(flower)
        return this
    }

    fun removeFlowers(flower: Flower): Collection {
        this.flowers.remove(flower)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Collection) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Collection{" +
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
