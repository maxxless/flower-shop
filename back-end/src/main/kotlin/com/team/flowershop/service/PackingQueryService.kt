package com.team.flowershop.service

import com.team.flowershop.domain.Collection_
import com.team.flowershop.domain.Packing
import com.team.flowershop.domain.Packing_
import com.team.flowershop.repository.PackingRepository
import com.team.flowershop.repository.search.PackingSearchRepository
import com.team.flowershop.service.dto.PackingCriteria
import io.github.jhipster.service.QueryService
import javax.persistence.criteria.JoinType
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for executing complex queries for [Packing] entities in the database.
 * The main input is a [PackingCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Packing] or a [Page] of [Packing] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class PackingQueryService(
    private val packingRepository: PackingRepository,
    private val packingSearchRepository: PackingSearchRepository
) : QueryService<Packing>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [Packing] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: PackingCriteria?): MutableList<Packing> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return packingRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Packing] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: PackingCriteria?, page: Pageable): Page<Packing> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return packingRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: PackingCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return packingRepository.count(specification)
    }

    /**
     * Function to convert [PackingCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: PackingCriteria?): Specification<Packing?> {
        var specification: Specification<Packing?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, Packing_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Packing_.name))
            }
            if (criteria.material != null) {
                specification = specification.and(buildStringSpecification(criteria.material, Packing_.material))
            }
            if (criteria.price != null) {
                specification = specification.and(buildRangeSpecification(criteria.price, Packing_.price))
            }
            if (criteria.collectionsId != null) {
                specification = specification.and(buildSpecification(criteria.collectionsId) {
                    it.join(Packing_.collections, JoinType.LEFT).get(Collection_.id)
                })
            }
        }
        return specification
    }
}
