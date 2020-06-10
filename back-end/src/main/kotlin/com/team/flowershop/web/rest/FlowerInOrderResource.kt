package com.team.flowershop.web.rest

import com.team.flowershop.domain.FlowerInOrder
import com.team.flowershop.service.FlowerInOrderService
import com.team.flowershop.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private const val ENTITY_NAME = "flowerInOrder"
/**
 * REST controller for managing [com.team.flowershop.domain.FlowerInOrder].
 */
@RestController
@RequestMapping("/api")
class FlowerInOrderResource(
    private val flowerInOrderService: FlowerInOrderService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /flower-in-orders` : Create a new flowerInOrder.
     *
     * @param flowerInOrder the flowerInOrder to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new flowerInOrder, or with status `400 (Bad Request)` if the flowerInOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/flower-in-orders")
    fun createFlowerInOrder(@RequestBody flowerInOrder: FlowerInOrder): ResponseEntity<FlowerInOrder> {
        log.debug("REST request to save FlowerInOrder : {}", flowerInOrder)
        if (flowerInOrder.id != null) {
            throw BadRequestAlertException(
                "A new flowerInOrder cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = flowerInOrderService.save(flowerInOrder)
        return ResponseEntity.created(URI("/api/flower-in-orders/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /flower-in-orders` : Updates an existing flowerInOrder.
     *
     * @param flowerInOrder the flowerInOrder to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated flowerInOrder,
     * or with status `400 (Bad Request)` if the flowerInOrder is not valid,
     * or with status `500 (Internal Server Error)` if the flowerInOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/flower-in-orders")
    fun updateFlowerInOrder(@RequestBody flowerInOrder: FlowerInOrder): ResponseEntity<FlowerInOrder> {
        log.debug("REST request to update FlowerInOrder : {}", flowerInOrder)
        if (flowerInOrder.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = flowerInOrderService.save(flowerInOrder)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     flowerInOrder.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /flower-in-orders` : get all the flowerInOrders.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of flowerInOrders in body.
     */
    @GetMapping("/flower-in-orders")
    fun getAllFlowerInOrders(): MutableList<FlowerInOrder> {
        log.debug("REST request to get all FlowerInOrders")
        return flowerInOrderService.findAll()
    }

    /**
     * `GET  /flower-in-orders/:id` : get the "id" flowerInOrder.
     *
     * @param id the id of the flowerInOrder to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the flowerInOrder, or with status `404 (Not Found)`.
     */
    @GetMapping("/flower-in-orders/{id}")
    fun getFlowerInOrder(@PathVariable id: Long): ResponseEntity<FlowerInOrder> {
        log.debug("REST request to get FlowerInOrder : {}", id)
        val flowerInOrder = flowerInOrderService.findOne(id)
        return ResponseUtil.wrapOrNotFound(flowerInOrder)
    }
    /**
     *  `DELETE  /flower-in-orders/:id` : delete the "id" flowerInOrder.
     *
     * @param id the id of the flowerInOrder to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/flower-in-orders/{id}")
    fun deleteFlowerInOrder(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete FlowerInOrder : {}", id)
        flowerInOrderService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/flower-in-orders?query=:query` : search for the flowerInOrder corresponding
     * to the query.
     *
     * @param query the query of the flowerInOrder search.
     * @return the result of the search.
     */
    @GetMapping("/_search/flower-in-orders")
    fun searchFlowerInOrders(@RequestParam query: String): MutableList<FlowerInOrder> {
        log.debug("REST request to search FlowerInOrders for query {}", query)
        return flowerInOrderService.search(query).toMutableList()
    }
}
