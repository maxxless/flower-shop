package com.team.flowershop.service.impl

import com.team.flowershop.domain.Cart
import com.team.flowershop.repository.CartRepository
import com.team.flowershop.repository.UserRepository
import com.team.flowershop.repository.search.CartSearchRepository
import com.team.flowershop.service.CartService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Cart].
 */
@Service
@Transactional
class CartServiceImpl(
    private val cartRepository: CartRepository,
    private val cartSearchRepository: CartSearchRepository,
    private val userRepository: UserRepository
) : CartService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a cart.
     *
     * @param cart the entity to save.
     * @return the persisted entity.
     */
    override fun save(cart: Cart): Cart {
        log.debug("Request to save Cart : {}", cart)
        val userId = cart.user?.id
        if (userId != null) {
            userRepository.findById(userId)
                .ifPresent { cart.user = it }
        }
        val result = cartRepository.save(cart)
        cartSearchRepository.save(result)
        return result
    }

    /**
     * Get all the carts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<Cart> {
        log.debug("Request to get all Carts")
        return cartRepository.findAll()
    }

    /**
     * Get one cart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Cart> {
        log.debug("Request to get Cart : {}", id)
        return cartRepository.findById(id)
    }

    /**
     * Delete the cart by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Cart : {}", id)

        cartRepository.deleteById(id)
        cartSearchRepository.deleteById(id)
    }

    /**
     * Search for the cart corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<Cart> {
        log.debug("Request to search Carts for query {}", query)
        return cartSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
