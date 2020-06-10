package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

/**
 * A Cart.
 */
@Entity
@Table(name = "cart")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cart")
data class Cart(
    @Id
    var id: Long? = null,
    @Column(name = "total_price_without_discount")
    var totalPriceWithoutDiscount: Double? = null,

    @Column(name = "card_discount")
    var cardDiscount: Double? = null,

    @Column(name = "bonus_discount")
    var bonusDiscount: Double? = null,

    @Column(name = "final_price")
    var finalPrice: Double? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnore
    var user: User? = null,

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER)
    var collectionDetails: MutableSet<CollectionInCart> = mutableSetOf(),

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER)
    var flowerDetails: MutableSet<FlowerInCart> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addCollectionDetails(collectionInCart: CollectionInCart): Cart {
        this.collectionDetails.add(collectionInCart)
        collectionInCart.cart = this
        return this
    }

    fun removeCollectionDetails(collectionInCart: CollectionInCart): Cart {
        this.collectionDetails.remove(collectionInCart)
        collectionInCart.cart = null
        return this
    }

    fun addFlowerDetails(flowerInCart: FlowerInCart): Cart {
        this.flowerDetails.add(flowerInCart)
        flowerInCart.cart = this
        return this
    }

    fun removeFlowerDetails(flowerInCart: FlowerInCart): Cart {
        this.flowerDetails.remove(flowerInCart)
        flowerInCart.cart = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cart) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Cart{" +
        "id=$id" +
        ", totalPriceWithoutDiscount=$totalPriceWithoutDiscount" +
        ", cardDiscount=$cardDiscount" +
        ", bonusDiscount=$bonusDiscount" +
        ", finalPrice=$finalPrice" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
