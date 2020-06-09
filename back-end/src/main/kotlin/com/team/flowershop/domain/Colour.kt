package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Colour.
 */
@Entity
@Table(name = "colour")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "colour")
data class Colour(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null,

    @ManyToMany(mappedBy = "availableColours")
    @JsonIgnore
    var flowers: MutableSet<Flower> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addFlowers(flower: Flower): Colour {
        this.flowers.add(flower)
        flower.availableColours.add(this)
        return this
    }

    fun removeFlowers(flower: Flower): Colour {
        this.flowers.remove(flower)
        flower.availableColours.remove(this)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Colour) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Colour{" +
        "id=$id" +
        ", name='$name'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
