package com.team.flowershop.service.impl

import com.team.flowershop.domain.Order
import com.team.flowershop.repository.OrderRepository
import com.team.flowershop.repository.search.OrderSearchRepository
import com.team.flowershop.service.OrderService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Order].
 */
@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderSearchRepository: OrderSearchRepository
) : OrderService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    override fun save(order: Order): Order {
        log.debug("Request to save Order : {}", order)
        val result = orderRepository.save(order)
        orderSearchRepository.save(result)
        return result
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Order> {
        log.debug("Request to get all Orders")
        return orderRepository.findAll(pageable)
    }

    /**
     *  Get all the orders where Delivery is `null`.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAllWhereDeliveryIsNull(): MutableList<Order> {
        log.debug("Request to get all orders where Delivery is null")
        return orderRepository.findAll()
            .filterTo(mutableListOf()) { it.delivery == null }
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Order> {
        log.debug("Request to get Order : {}", id)
        return orderRepository.findById(id)
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Order : {}", id)

        orderRepository.deleteById(id)
        orderSearchRepository.deleteById(id)
    }

    /**
     * Search for the order corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String, pageable: Pageable): Page<Order> {
        log.debug("Request to search for a page of Orders for query {}", query)
        return orderSearchRepository.search(queryStringQuery(query), pageable)
    }
}
