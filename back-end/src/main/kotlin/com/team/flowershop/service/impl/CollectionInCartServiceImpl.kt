package com.team.flowershop.service.impl

import com.team.flowershop.domain.CollectionInCart
import com.team.flowershop.repository.CollectionInCartRepository
import com.team.flowershop.repository.search.CollectionInCartSearchRepository
import com.team.flowershop.service.CollectionInCartService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [CollectionInCart].
 */
@Service
@Transactional
class CollectionInCartServiceImpl(
    private val collectionInCartRepository: CollectionInCartRepository,
    private val collectionInCartSearchRepository: CollectionInCartSearchRepository
) : CollectionInCartService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a collectionInCart.
     *
     * @param collectionInCart the entity to save.
     * @return the persisted entity.
     */
    override fun save(collectionInCart: CollectionInCart): CollectionInCart {
        log.debug("Request to save CollectionInCart : {}", collectionInCart)
        val result = collectionInCartRepository.save(collectionInCart)
        collectionInCartSearchRepository.save(result)
        return result
    }

    /**
     * Get all the collectionInCarts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<CollectionInCart> {
        log.debug("Request to get all CollectionInCarts")
        return collectionInCartRepository.findAll()
    }

    /**
     * Get one collectionInCart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<CollectionInCart> {
        log.debug("Request to get CollectionInCart : {}", id)
        return collectionInCartRepository.findById(id)
    }

    /**
     * Delete the collectionInCart by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete CollectionInCart : {}", id)

        collectionInCartRepository.deleteById(id)
        collectionInCartSearchRepository.deleteById(id)
    }

    /**
     * Search for the collectionInCart corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<CollectionInCart> {
        log.debug("Request to search CollectionInCarts for query {}", query)
        return collectionInCartSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
