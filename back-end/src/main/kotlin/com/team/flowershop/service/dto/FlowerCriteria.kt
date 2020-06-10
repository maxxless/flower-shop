package com.team.flowershop.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.DoubleFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.team.flowershop.domain.Flower] entity. This class is used in
 * [com.team.flowershop.web.rest.FlowerResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/flowers?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class FlowerCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var description: StringFilter? = null,

    var price: DoubleFilter? = null,

    var availableColoursId: LongFilter? = null,

    var collectionsInId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: FlowerCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.description?.copy(),
            other.price?.copy(),
            other.availableColoursId?.copy(),
            other.collectionsInId?.copy()
        )

    override fun copy() = FlowerCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
