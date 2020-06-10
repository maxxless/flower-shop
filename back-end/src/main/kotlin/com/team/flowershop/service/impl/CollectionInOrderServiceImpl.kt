package com.team.flowershop.service.impl

import com.team.flowershop.domain.CollectionInOrder
import com.team.flowershop.repository.CollectionInOrderRepository
import com.team.flowershop.repository.search.CollectionInOrderSearchRepository
import com.team.flowershop.service.CollectionInOrderService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [CollectionInOrder].
 */
@Service
@Transactional
class CollectionInOrderServiceImpl(
    private val collectionInOrderRepository: CollectionInOrderRepository,
    private val collectionInOrderSearchRepository: CollectionInOrderSearchRepository
) : CollectionInOrderService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a collectionInOrder.
     *
     * @param collectionInOrder the entity to save.
     * @return the persisted entity.
     */
    override fun save(collectionInOrder: CollectionInOrder): CollectionInOrder {
        log.debug("Request to save CollectionInOrder : {}", collectionInOrder)
        val result = collectionInOrderRepository.save(collectionInOrder)
        collectionInOrderSearchRepository.save(result)
        return result
    }

    /**
     * Get all the collectionInOrders.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<CollectionInOrder> {
        log.debug("Request to get all CollectionInOrders")
        return collectionInOrderRepository.findAll()
    }

    /**
     * Get one collectionInOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<CollectionInOrder> {
        log.debug("Request to get CollectionInOrder : {}", id)
        return collectionInOrderRepository.findById(id)
    }

    /**
     * Delete the collectionInOrder by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete CollectionInOrder : {}", id)

        collectionInOrderRepository.deleteById(id)
        collectionInOrderSearchRepository.deleteById(id)
    }

    /**
     * Search for the collectionInOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<CollectionInOrder> {
        log.debug("Request to search CollectionInOrders for query {}", query)
        return collectionInOrderSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
