package com.team.flowershop.service.impl

import com.team.flowershop.domain.FlowerInOrder
import com.team.flowershop.repository.FlowerInOrderRepository
import com.team.flowershop.repository.search.FlowerInOrderSearchRepository
import com.team.flowershop.service.FlowerInOrderService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [FlowerInOrder].
 */
@Service
@Transactional
class FlowerInOrderServiceImpl(
    private val flowerInOrderRepository: FlowerInOrderRepository,
    private val flowerInOrderSearchRepository: FlowerInOrderSearchRepository
) : FlowerInOrderService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a flowerInOrder.
     *
     * @param flowerInOrder the entity to save.
     * @return the persisted entity.
     */
    override fun save(flowerInOrder: FlowerInOrder): FlowerInOrder {
        log.debug("Request to save FlowerInOrder : {}", flowerInOrder)
        val result = flowerInOrderRepository.save(flowerInOrder)
        flowerInOrderSearchRepository.save(result)
        return result
    }

    /**
     * Get all the flowerInOrders.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<FlowerInOrder> {
        log.debug("Request to get all FlowerInOrders")
        return flowerInOrderRepository.findAll()
    }

    /**
     * Get one flowerInOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<FlowerInOrder> {
        log.debug("Request to get FlowerInOrder : {}", id)
        return flowerInOrderRepository.findById(id)
    }

    /**
     * Delete the flowerInOrder by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete FlowerInOrder : {}", id)

        flowerInOrderRepository.deleteById(id)
        flowerInOrderSearchRepository.deleteById(id)
    }

    /**
     * Search for the flowerInOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<FlowerInOrder> {
        log.debug("Request to search FlowerInOrders for query {}", query)
        return flowerInOrderSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
