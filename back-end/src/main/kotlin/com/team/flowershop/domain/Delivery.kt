package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.team.flowershop.domain.enumeration.DeliveryType
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Delivery.
 */
@Entity
@Table(name = "delivery")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "delivery")
data class Delivery(
    @Id
    var id: Long? = null,
    @get: NotNull
    @Column(name = "address", nullable = false)
    var address: String? = null,

    @Column(name = "post_office_number")
    var postOfficeNumber: Int? = null,

    @Column(name = "price")
    var price: Double? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: DeliveryType? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    var order: Order? = null,

    @ManyToOne @JsonManagedReference("user-deliveries")
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Delivery) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Delivery{" +
        "id=$id" +
        ", address='$address'" +
        ", postOfficeNumber=$postOfficeNumber" +
        ", price=$price" +
        ", type='$type'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
