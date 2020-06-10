package com.team.flowershop.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.DoubleFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.team.flowershop.domain.Collection] entity. This class is used in
 * [com.team.flowershop.web.rest.CollectionResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/collections?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class CollectionCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var description: StringFilter? = null,

    var price: DoubleFilter? = null,

    var availablePackingsId: LongFilter? = null,

    var flowersId: LongFilter? = null,

    var categoryId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: CollectionCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.description?.copy(),
            other.price?.copy(),
            other.availablePackingsId?.copy(),
            other.flowersId?.copy(),
            other.categoryId?.copy()
        )

    override fun copy() = CollectionCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
