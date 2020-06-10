package com.team.flowershop.service.impl

import com.team.flowershop.domain.FlowerInCart
import com.team.flowershop.repository.FlowerInCartRepository
import com.team.flowershop.repository.search.FlowerInCartSearchRepository
import com.team.flowershop.service.FlowerInCartService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [FlowerInCart].
 */
@Service
@Transactional
class FlowerInCartServiceImpl(
    private val flowerInCartRepository: FlowerInCartRepository,
    private val flowerInCartSearchRepository: FlowerInCartSearchRepository
) : FlowerInCartService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a flowerInCart.
     *
     * @param flowerInCart the entity to save.
     * @return the persisted entity.
     */
    override fun save(flowerInCart: FlowerInCart): FlowerInCart {
        log.debug("Request to save FlowerInCart : {}", flowerInCart)
        val result = flowerInCartRepository.save(flowerInCart)
        flowerInCartSearchRepository.save(result)
        return result
    }

    /**
     * Get all the flowerInCarts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<FlowerInCart> {
        log.debug("Request to get all FlowerInCarts")
        return flowerInCartRepository.findAll()
    }

    /**
     * Get one flowerInCart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<FlowerInCart> {
        log.debug("Request to get FlowerInCart : {}", id)
        return flowerInCartRepository.findById(id)
    }

    /**
     * Delete the flowerInCart by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete FlowerInCart : {}", id)

        flowerInCartRepository.deleteById(id)
        flowerInCartSearchRepository.deleteById(id)
    }

    /**
     * Search for the flowerInCart corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<FlowerInCart> {
        log.debug("Request to search FlowerInCarts for query {}", query)
        return flowerInCartSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
