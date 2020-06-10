package com.team.flowershop.service
import com.team.flowershop.domain.CollectionInCart
import java.util.Optional

/**
 * Service Interface for managing [CollectionInCart].
 */
interface CollectionInCartService {

    /**
     * Save a collectionInCart.
     *
     * @param collectionInCart the entity to save.
     * @return the persisted entity.
     */
    fun save(collectionInCart: CollectionInCart): CollectionInCart

    /**
     * Get all the collectionInCarts.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<CollectionInCart>

    /**
     * Get the "id" collectionInCart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<CollectionInCart>

    /**
     * Delete the "id" collectionInCart.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the collectionInCart corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<CollectionInCart>
}
