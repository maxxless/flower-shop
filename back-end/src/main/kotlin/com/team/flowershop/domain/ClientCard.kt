package com.team.flowershop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.team.flowershop.domain.enumeration.CardType
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A ClientCard.
 */
@Entity
@Table(name = "client_card")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "clientcard")
data class ClientCard(
    @Id
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: CardType? = null,

    @Column(name = "bonus_amount")
    var bonusAmount: Double? = null,

    @Column(name = "percentage")
    var percentage: Double? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnore
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientCard) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ClientCard{" +
        "id=$id" +
        ", name='$name'" +
        ", description='$description'" +
        ", type='$type'" +
        ", bonusAmount=$bonusAmount" +
        ", percentage=$percentage" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
