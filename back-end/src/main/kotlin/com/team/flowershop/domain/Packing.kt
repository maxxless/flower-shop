package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Packing.
 */
@Entity
@Table(name = "packing")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "packing")
data class Packing(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "material")
    var material: String? = null,

    @get: NotNull
    @Column(name = "price", nullable = false)
    var price: Double? = null,

    @ManyToMany(mappedBy = "availablePackings")
    @JsonIgnore
    var collections: MutableSet<Collection> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addCollections(collection: Collection): Packing {
        this.collections.add(collection)
        collection.availablePackings.add(this)
        return this
    }

    fun removeCollections(collection: Collection): Packing {
        this.collections.remove(collection)
        collection.availablePackings.remove(this)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Packing) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Packing{" +
        "id=$id" +
        ", name='$name'" +
        ", material='$material'" +
        ", price=$price" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
