package com.team.flowershop.web.rest

import com.team.flowershop.domain.Delivery
import com.team.flowershop.service.DeliveryService
import com.team.flowershop.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "delivery"
/**
 * REST controller for managing [com.team.flowershop.domain.Delivery].
 */
@RestController
@RequestMapping("/api")
class DeliveryResource(
    private val deliveryService: DeliveryService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /deliveries` : Create a new delivery.
     *
     * @param delivery the delivery to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new delivery, or with status `400 (Bad Request)` if the delivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/deliveries")
    fun createDelivery(@Valid @RequestBody delivery: Delivery): ResponseEntity<Delivery> {
        log.debug("REST request to save Delivery : {}", delivery)
        if (delivery.id != null) {
            throw BadRequestAlertException(
                "A new delivery cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        if (Objects.isNull(delivery.order)) {
            throw BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null")
        }
        val result = deliveryService.save(delivery)
        return ResponseEntity.created(URI("/api/deliveries/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /deliveries` : Updates an existing delivery.
     *
     * @param delivery the delivery to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated delivery,
     * or with status `400 (Bad Request)` if the delivery is not valid,
     * or with status `500 (Internal Server Error)` if the delivery couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/deliveries")
    fun updateDelivery(@Valid @RequestBody delivery: Delivery): ResponseEntity<Delivery> {
        log.debug("REST request to update Delivery : {}", delivery)
        if (delivery.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = deliveryService.save(delivery)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     delivery.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /deliveries` : get all the deliveries.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of deliveries in body.
     */
    @GetMapping("/deliveries")
    fun getAllDeliveries(
        pageable: Pageable
    ): ResponseEntity<MutableList<Delivery>> {
        log.debug("REST request to get a page of Deliveries")
        val page = deliveryService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /deliveries/:id` : get the "id" delivery.
     *
     * @param id the id of the delivery to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the delivery, or with status `404 (Not Found)`.
     */
    @GetMapping("/deliveries/{id}")
    fun getDelivery(@PathVariable id: Long): ResponseEntity<Delivery> {
        log.debug("REST request to get Delivery : {}", id)
        val delivery = deliveryService.findOne(id)
        return ResponseUtil.wrapOrNotFound(delivery)
    }
    /**
     *  `DELETE  /deliveries/:id` : delete the "id" delivery.
     *
     * @param id the id of the delivery to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/deliveries/{id}")
    fun deleteDelivery(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Delivery : {}", id)
        deliveryService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/deliveries?query=:query` : search for the delivery corresponding
     * to the query.
     *
     * @param query the query of the delivery search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/deliveries")
    fun searchDeliveries(@RequestParam query: String, pageable: Pageable): ResponseEntity<MutableList<Delivery>> {
        log.debug("REST request to search for a page of Deliveries for query {}", query)
        val page = deliveryService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }
}
