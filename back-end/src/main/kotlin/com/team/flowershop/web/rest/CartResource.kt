package com.team.flowershop.web.rest

import com.team.flowershop.domain.Cart
import com.team.flowershop.service.CartService
import com.team.flowershop.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
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

private const val ENTITY_NAME = "cart"
/**
 * REST controller for managing [com.team.flowershop.domain.Cart].
 */
@RestController
@RequestMapping("/api")
class CartResource(
    private val cartService: CartService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /carts` : Create a new cart.
     *
     * @param cart the cart to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new cart, or with status `400 (Bad Request)` if the cart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carts")
    fun createCart(@RequestBody cart: Cart): ResponseEntity<Cart> {
        log.debug("REST request to save Cart : {}", cart)
        if (cart.id != null) {
            throw BadRequestAlertException(
                "A new cart cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        if (Objects.isNull(cart.user)) {
            throw BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null")
        }
        val result = cartService.save(cart)
        return ResponseEntity.created(URI("/api/carts/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /carts` : Updates an existing cart.
     *
     * @param cart the cart to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated cart,
     * or with status `400 (Bad Request)` if the cart is not valid,
     * or with status `500 (Internal Server Error)` if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carts")
    fun updateCart(@RequestBody cart: Cart): ResponseEntity<Cart> {
        log.debug("REST request to update Cart : {}", cart)
        if (cart.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = cartService.save(cart)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     cart.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /carts` : get all the carts.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of carts in body.
     */
    @GetMapping("/carts")
    fun getAllCarts(): MutableList<Cart> {
        log.debug("REST request to get all Carts")
        return cartService.findAll()
    }

    /**
     * `GET  /carts/:id` : get the "id" cart.
     *
     * @param id the id of the cart to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the cart, or with status `404 (Not Found)`.
     */
    @GetMapping("/carts/{id}")
    fun getCart(@PathVariable id: Long): ResponseEntity<Cart> {
        log.debug("REST request to get Cart : {}", id)
        val cart = cartService.findOne(id)
        return ResponseUtil.wrapOrNotFound(cart)
    }
    /**
     *  `DELETE  /carts/:id` : delete the "id" cart.
     *
     * @param id the id of the cart to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/carts/{id}")
    fun deleteCart(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Cart : {}", id)
        cartService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/carts?query=:query` : search for the cart corresponding
     * to the query.
     *
     * @param query the query of the cart search.
     * @return the result of the search.
     */
    @GetMapping("/_search/carts")
    fun searchCarts(@RequestParam query: String): MutableList<Cart> {
        log.debug("REST request to search Carts for query {}", query)
        return cartService.search(query).toMutableList()
    }
}
