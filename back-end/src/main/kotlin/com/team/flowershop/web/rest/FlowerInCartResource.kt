package com.team.flowershop.web.rest

import com.team.flowershop.domain.FlowerInCart
import com.team.flowershop.service.FlowerInCartService
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

private const val ENTITY_NAME = "flowerInCart"
/**
 * REST controller for managing [com.team.flowershop.domain.FlowerInCart].
 */
@RestController
@RequestMapping("/api")
class FlowerInCartResource(
    private val flowerInCartService: FlowerInCartService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /flower-in-carts` : Create a new flowerInCart.
     *
     * @param flowerInCart the flowerInCart to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new flowerInCart, or with status `400 (Bad Request)` if the flowerInCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/flower-in-carts")
    fun createFlowerInCart(@RequestBody flowerInCart: FlowerInCart): ResponseEntity<FlowerInCart> {
        log.debug("REST request to save FlowerInCart : {}", flowerInCart)
        if (flowerInCart.id != null) {
            throw BadRequestAlertException(
                "A new flowerInCart cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = flowerInCartService.save(flowerInCart)
        return ResponseEntity.created(URI("/api/flower-in-carts/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /flower-in-carts` : Updates an existing flowerInCart.
     *
     * @param flowerInCart the flowerInCart to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated flowerInCart,
     * or with status `400 (Bad Request)` if the flowerInCart is not valid,
     * or with status `500 (Internal Server Error)` if the flowerInCart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/flower-in-carts")
    fun updateFlowerInCart(@RequestBody flowerInCart: FlowerInCart): ResponseEntity<FlowerInCart> {
        log.debug("REST request to update FlowerInCart : {}", flowerInCart)
        if (flowerInCart.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = flowerInCartService.save(flowerInCart)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     flowerInCart.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /flower-in-carts` : get all the flowerInCarts.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of flowerInCarts in body.
     */
    @GetMapping("/flower-in-carts")
    fun getAllFlowerInCarts(): MutableList<FlowerInCart> {
        log.debug("REST request to get all FlowerInCarts")
        return flowerInCartService.findAll()
    }

    /**
     * `GET  /flower-in-carts/:id` : get the "id" flowerInCart.
     *
     * @param id the id of the flowerInCart to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the flowerInCart, or with status `404 (Not Found)`.
     */
    @GetMapping("/flower-in-carts/{id}")
    fun getFlowerInCart(@PathVariable id: Long): ResponseEntity<FlowerInCart> {
        log.debug("REST request to get FlowerInCart : {}", id)
        val flowerInCart = flowerInCartService.findOne(id)
        return ResponseUtil.wrapOrNotFound(flowerInCart)
    }
    /**
     *  `DELETE  /flower-in-carts/:id` : delete the "id" flowerInCart.
     *
     * @param id the id of the flowerInCart to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/flower-in-carts/{id}")
    fun deleteFlowerInCart(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete FlowerInCart : {}", id)
        flowerInCartService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/flower-in-carts?query=:query` : search for the flowerInCart corresponding
     * to the query.
     *
     * @param query the query of the flowerInCart search.
     * @return the result of the search.
     */
    @GetMapping("/_search/flower-in-carts")
    fun searchFlowerInCarts(@RequestParam query: String): MutableList<FlowerInCart> {
        log.debug("REST request to search FlowerInCarts for query {}", query)
        return flowerInCartService.search(query).toMutableList()
    }
}
