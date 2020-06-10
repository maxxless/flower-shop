package com.team.flowershop.domain

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "category")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null,

    @OneToMany(mappedBy = "category")
    var collections: MutableSet<Collection> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addCollections(collection: Collection): Category {
        this.collections.add(collection)
        collection.category = this
        return this
    }

    fun removeCollections(collection: Collection): Category {
        this.collections.remove(collection)
        collection.category = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Category{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
