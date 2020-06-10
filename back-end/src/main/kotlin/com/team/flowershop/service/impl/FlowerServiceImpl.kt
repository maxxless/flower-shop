package com.team.flowershop.service.impl

import com.team.flowershop.domain.Flower
import com.team.flowershop.repository.FlowerRepository
import com.team.flowershop.repository.search.FlowerSearchRepository
import com.team.flowershop.service.FlowerService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Flower].
 */
@Service
@Transactional
class FlowerServiceImpl(
    private val flowerRepository: FlowerRepository,
    private val flowerSearchRepository: FlowerSearchRepository
) : FlowerService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a flower.
     *
     * @param flower the entity to save.
     * @return the persisted entity.
     */
    override fun save(flower: Flower): Flower {
        log.debug("Request to save Flower : {}", flower)
        val result = flowerRepository.save(flower)
        flowerSearchRepository.save(result)
        return result
    }

    /**
     * Get all the flowers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Flower> {
        log.debug("Request to get all Flowers")
        return flowerRepository.findAll(pageable)
    }

    /**
     * Get all the flowers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    override fun findAllWithEagerRelationships(pageable: Pageable) =
        flowerRepository.findAllWithEagerRelationships(pageable)

    /**
     * Get one flower by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Flower> {
        log.debug("Request to get Flower : {}", id)
        return flowerRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the flower by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Flower : {}", id)

        flowerRepository.deleteById(id)
        flowerSearchRepository.deleteById(id)
    }

    /**
     * Search for the flower corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String, pageable: Pageable): Page<Flower> {
        log.debug("Request to search for a page of Flowers for query {}", query)
        return flowerSearchRepository.search(queryStringQuery(query), pageable)
    }
}
