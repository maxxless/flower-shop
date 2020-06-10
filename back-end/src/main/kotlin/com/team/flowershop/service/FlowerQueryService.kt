package com.team.flowershop.service

import com.team.flowershop.domain.Collection_
import com.team.flowershop.domain.Colour_
import com.team.flowershop.domain.Flower
import com.team.flowershop.domain.Flower_
import com.team.flowershop.repository.FlowerRepository
import com.team.flowershop.repository.search.FlowerSearchRepository
import com.team.flowershop.service.dto.FlowerCriteria
import io.github.jhipster.service.QueryService
import javax.persistence.criteria.JoinType
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for executing complex queries for [Flower] entities in the database.
 * The main input is a [FlowerCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Flower] or a [Page] of [Flower] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class FlowerQueryService(
    private val flowerRepository: FlowerRepository,
    private val flowerSearchRepository: FlowerSearchRepository
) : QueryService<Flower>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [Flower] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: FlowerCriteria?): MutableList<Flower> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return flowerRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Flower] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: FlowerCriteria?, page: Pageable): Page<Flower> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return flowerRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: FlowerCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return flowerRepository.count(specification)
    }

    /**
     * Function to convert [FlowerCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: FlowerCriteria?): Specification<Flower?> {
        var specification: Specification<Flower?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, Flower_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Flower_.name))
            }
            if (criteria.description != null) {
                specification = specification.and(buildStringSpecification(criteria.description, Flower_.description))
            }
            if (criteria.price != null) {
                specification = specification.and(buildRangeSpecification(criteria.price, Flower_.price))
            }
            if (criteria.availableColoursId != null) {
                specification = specification.and(buildSpecification(criteria.availableColoursId) {
                    it.join(Flower_.availableColours, JoinType.LEFT).get(Colour_.id)
                })
            }
            if (criteria.collectionsInId != null) {
                specification = specification.and(buildSpecification(criteria.collectionsInId) {
                    it.join(Flower_.collectionsIns, JoinType.LEFT).get(Collection_.id)
                })
            }
        }
        return specification
    }
}
