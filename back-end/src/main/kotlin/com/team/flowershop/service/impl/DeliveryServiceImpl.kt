package com.team.flowershop.service.impl

import com.team.flowershop.domain.Delivery
import com.team.flowershop.repository.DeliveryRepository
import com.team.flowershop.repository.OrderRepository
import com.team.flowershop.repository.search.DeliverySearchRepository
import com.team.flowershop.service.DeliveryService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Delivery].
 */
@Service
@Transactional
class DeliveryServiceImpl(
    private val deliveryRepository: DeliveryRepository,
    private val deliverySearchRepository: DeliverySearchRepository,
    private val orderRepository: OrderRepository
) : DeliveryService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a delivery.
     *
     * @param delivery the entity to save.
     * @return the persisted entity.
     */
    override fun save(delivery: Delivery): Delivery {
        log.debug("Request to save Delivery : {}", delivery)
        val orderId = delivery.order?.id
        if (orderId != null) {
            orderRepository.findById(orderId)
                .ifPresent { delivery.order = it }
        }
        val result = deliveryRepository.save(delivery)
        deliverySearchRepository.save(result)
        return result
    }

    /**
     * Get all the deliveries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Delivery> {
        log.debug("Request to get all Deliveries")
        return deliveryRepository.findAll(pageable)
    }

    /**
     * Get one delivery by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Delivery> {
        log.debug("Request to get Delivery : {}", id)
        return deliveryRepository.findById(id)
    }

    /**
     * Delete the delivery by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Delivery : {}", id)

        deliveryRepository.deleteById(id)
        deliverySearchRepository.deleteById(id)
    }

    /**
     * Search for the delivery corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String, pageable: Pageable): Page<Delivery> {
        log.debug("Request to search for a page of Deliveries for query {}", query)
        return deliverySearchRepository.search(queryStringQuery(query), pageable)
    }
}
