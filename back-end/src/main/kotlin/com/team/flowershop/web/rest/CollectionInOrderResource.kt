package com.team.flowershop.web.rest

import com.team.flowershop.domain.CollectionInOrder
import com.team.flowershop.service.CollectionInOrderService
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

private const val ENTITY_NAME = "collectionInOrder"
/**
 * REST controller for managing [com.team.flowershop.domain.CollectionInOrder].
 */
@RestController
@RequestMapping("/api")
class CollectionInOrderResource(
    private val collectionInOrderService: CollectionInOrderService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /collection-in-orders` : Create a new collectionInOrder.
     *
     * @param collectionInOrder the collectionInOrder to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new collectionInOrder, or with status `400 (Bad Request)` if the collectionInOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/collection-in-orders")
    fun createCollectionInOrder(@RequestBody collectionInOrder: CollectionInOrder): ResponseEntity<CollectionInOrder> {
        log.debug("REST request to save CollectionInOrder : {}", collectionInOrder)
        if (collectionInOrder.id != null) {
            throw BadRequestAlertException(
                "A new collectionInOrder cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = collectionInOrderService.save(collectionInOrder)
        return ResponseEntity.created(URI("/api/collection-in-orders/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /collection-in-orders` : Updates an existing collectionInOrder.
     *
     * @param collectionInOrder the collectionInOrder to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated collectionInOrder,
     * or with status `400 (Bad Request)` if the collectionInOrder is not valid,
     * or with status `500 (Internal Server Error)` if the collectionInOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/collection-in-orders")
    fun updateCollectionInOrder(@RequestBody collectionInOrder: CollectionInOrder): ResponseEntity<CollectionInOrder> {
        log.debug("REST request to update CollectionInOrder : {}", collectionInOrder)
        if (collectionInOrder.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = collectionInOrderService.save(collectionInOrder)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     collectionInOrder.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /collection-in-orders` : get all the collectionInOrders.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of collectionInOrders in body.
     */
    @GetMapping("/collection-in-orders")
    fun getAllCollectionInOrders(): MutableList<CollectionInOrder> {
        log.debug("REST request to get all CollectionInOrders")
        return collectionInOrderService.findAll()
    }

    /**
     * `GET  /collection-in-orders/:id` : get the "id" collectionInOrder.
     *
     * @param id the id of the collectionInOrder to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the collectionInOrder, or with status `404 (Not Found)`.
     */
    @GetMapping("/collection-in-orders/{id}")
    fun getCollectionInOrder(@PathVariable id: Long): ResponseEntity<CollectionInOrder> {
        log.debug("REST request to get CollectionInOrder : {}", id)
        val collectionInOrder = collectionInOrderService.findOne(id)
        return ResponseUtil.wrapOrNotFound(collectionInOrder)
    }
    /**
     *  `DELETE  /collection-in-orders/:id` : delete the "id" collectionInOrder.
     *
     * @param id the id of the collectionInOrder to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/collection-in-orders/{id}")
    fun deleteCollectionInOrder(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete CollectionInOrder : {}", id)
        collectionInOrderService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/collection-in-orders?query=:query` : search for the collectionInOrder corresponding
     * to the query.
     *
     * @param query the query of the collectionInOrder search.
     * @return the result of the search.
     */
    @GetMapping("/_search/collection-in-orders")
    fun searchCollectionInOrders(@RequestParam query: String): MutableList<CollectionInOrder> {
        log.debug("REST request to search CollectionInOrders for query {}", query)
        return collectionInOrderService.search(query).toMutableList()
    }
}
