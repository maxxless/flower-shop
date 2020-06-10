package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.persistence.*

/**
 * A FlowerInCart.
 */
@Entity
@Table(name = "flower_in_cart")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "flowerincart")
data class FlowerInCart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "amount")
    var amount: Int? = null,

    @ManyToOne @JsonIgnoreProperties("flowerInCarts")
    var colour: Colour? = null,

    @ManyToOne @JsonIgnoreProperties("flowerInCarts")
    var flower: Flower? = null,

    @ManyToOne @JsonIgnore
    var cart: Cart? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlowerInCart) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "FlowerInCart{" +
        "id=$id" +
        ", amount=$amount" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
