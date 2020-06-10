package com.team.flowershop.service
import com.team.flowershop.domain.CollectionInOrder
import java.util.Optional

/**
 * Service Interface for managing [CollectionInOrder].
 */
interface CollectionInOrderService {

    /**
     * Save a collectionInOrder.
     *
     * @param collectionInOrder the entity to save.
     * @return the persisted entity.
     */
    fun save(collectionInOrder: CollectionInOrder): CollectionInOrder

    /**
     * Get all the collectionInOrders.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<CollectionInOrder>

    /**
     * Get the "id" collectionInOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<CollectionInOrder>

    /**
     * Delete the "id" collectionInOrder.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the collectionInOrder corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<CollectionInOrder>
}
