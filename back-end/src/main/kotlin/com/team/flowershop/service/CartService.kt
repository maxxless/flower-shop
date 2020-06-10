package com.team.flowershop.service
import com.team.flowershop.domain.Cart
import java.util.Optional

/**
 * Service Interface for managing [Cart].
 */
interface CartService {

    /**
     * Save a cart.
     *
     * @param cart the entity to save.
     * @return the persisted entity.
     */
    fun save(cart: Cart): Cart

    /**
     * Get all the carts.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<Cart>

    /**
     * Get the "id" cart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Cart>

    /**
     * Delete the "id" cart.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the cart corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<Cart>
}
