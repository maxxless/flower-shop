package com.team.flowershop.service

import com.team.flowershop.domain.Category_
import com.team.flowershop.domain.Collection
import com.team.flowershop.domain.Collection_
import com.team.flowershop.domain.Flower_
import com.team.flowershop.domain.Packing_
import com.team.flowershop.repository.CollectionRepository
import com.team.flowershop.repository.search.CollectionSearchRepository
import com.team.flowershop.service.dto.CollectionCriteria
import io.github.jhipster.service.QueryService
import javax.persistence.criteria.JoinType
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for executing complex queries for [Collection] entities in the database.
 * The main input is a [CollectionCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Collection] or a [Page] of [Collection] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class CollectionQueryService(
    private val collectionRepository: CollectionRepository,
    private val collectionSearchRepository: CollectionSearchRepository
) : QueryService<Collection>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [Collection] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CollectionCriteria?): MutableList<Collection> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return collectionRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Collection] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CollectionCriteria?, page: Pageable): Page<Collection> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return collectionRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: CollectionCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return collectionRepository.count(specification)
    }

    /**
     * Function to convert [CollectionCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: CollectionCriteria?): Specification<Collection?> {
        var specification: Specification<Collection?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, Collection_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Collection_.name))
            }
            if (criteria.description != null) {
                specification = specification.and(buildStringSpecification(criteria.description, Collection_.description))
            }
            if (criteria.price != null) {
                specification = specification.and(buildRangeSpecification(criteria.price, Collection_.price))
            }
            if (criteria.availablePackingsId != null) {
                specification = specification.and(buildSpecification(criteria.availablePackingsId) {
                    it.join(Collection_.availablePackings, JoinType.LEFT).get(Packing_.id)
                })
            }
            if (criteria.flowersId != null) {
                specification = specification.and(buildSpecification(criteria.flowersId) {
                    it.join(Collection_.flowers, JoinType.LEFT).get(Flower_.id)
                })
            }
            if (criteria.categoryId != null) {
                specification = specification.and(buildSpecification(criteria.categoryId) {
                    it.join(Collection_.category, JoinType.LEFT).get(Category_.id)
                })
            }
        }
        return specification
    }
}
