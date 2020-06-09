package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.io.Serializable
import java.time.Instant
import javax.persistence.*

/**
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "total_price")
    var totalPrice: Double? = null,

    @Column(name = "date")
    var date: Instant? = null,

    @OneToMany(mappedBy = "order")
    var collectionDetails: MutableSet<CollectionInOrder> = mutableSetOf(),

    @OneToMany(mappedBy = "order")
    var flowerDetails: MutableSet<FlowerInOrder> = mutableSetOf(),

    @ManyToOne @JsonManagedReference("user-orders")
    var user: User? = null,

    @ManyToOne @JsonIgnoreProperties("orders")
    var packing: Packing? = null,

    @OneToOne(mappedBy = "order")
    @JsonIgnore
    var delivery: Delivery? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addCollectionDetails(collectionInOrder: CollectionInOrder): Order {
        this.collectionDetails.add(collectionInOrder)
        collectionInOrder.order = this
        return this
    }

    fun removeCollectionDetails(collectionInOrder: CollectionInOrder): Order {
        this.collectionDetails.remove(collectionInOrder)
        collectionInOrder.order = null
        return this
    }

    fun addFlowerDetails(flowerInOrder: FlowerInOrder): Order {
        this.flowerDetails.add(flowerInOrder)
        flowerInOrder.order = this
        return this
    }

    fun removeFlowerDetails(flowerInOrder: FlowerInOrder): Order {
        this.flowerDetails.remove(flowerInOrder)
        flowerInOrder.order = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Order) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Order{" +
        "id=$id" +
        ", totalPrice=$totalPrice" +
        ", date='$date'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
