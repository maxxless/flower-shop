package com.team.flowershop.service
import com.team.flowershop.domain.Order
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service Interface for managing [Order].
 */
interface OrderService {

    /**
     * Save a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    fun save(order: Order): Order

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<Order>
    /**
     * Get all the [OrderDTO] where Delivery is `null`.
     *
     * @return the list of entities.
     */
    fun findAllWhereDeliveryIsNull(): MutableList<Order>

    /**
     * Get the "id" order.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Order>

    /**
     * Delete the "id" order.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the order corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun search(query: String, pageable: Pageable): Page<Order>
}
