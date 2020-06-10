package com.team.flowershop.service
import com.team.flowershop.domain.FlowerInCart
import java.util.Optional

/**
 * Service Interface for managing [FlowerInCart].
 */
interface FlowerInCartService {

    /**
     * Save a flowerInCart.
     *
     * @param flowerInCart the entity to save.
     * @return the persisted entity.
     */
    fun save(flowerInCart: FlowerInCart): FlowerInCart

    /**
     * Get all the flowerInCarts.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<FlowerInCart>

    /**
     * Get the "id" flowerInCart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<FlowerInCart>

    /**
     * Delete the "id" flowerInCart.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the flowerInCart corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<FlowerInCart>
}
