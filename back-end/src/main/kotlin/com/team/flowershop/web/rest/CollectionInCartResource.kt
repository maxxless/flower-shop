package com.team.flowershop.web.rest

import com.team.flowershop.domain.CollectionInCart
import com.team.flowershop.service.CollectionInCartService
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

private const val ENTITY_NAME = "collectionInCart"
/**
 * REST controller for managing [com.team.flowershop.domain.CollectionInCart].
 */
@RestController
@RequestMapping("/api")
class CollectionInCartResource(
    private val collectionInCartService: CollectionInCartService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /collection-in-carts` : Create a new collectionInCart.
     *
     * @param collectionInCart the collectionInCart to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new collectionInCart, or with status `400 (Bad Request)` if the collectionInCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/collection-in-carts")
    fun createCollectionInCart(@RequestBody collectionInCart: CollectionInCart): ResponseEntity<CollectionInCart> {
        log.debug("REST request to save CollectionInCart : {}", collectionInCart)
        if (collectionInCart.id != null) {
            throw BadRequestAlertException(
                "A new collectionInCart cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = collectionInCartService.save(collectionInCart)
        return ResponseEntity.created(URI("/api/collection-in-carts/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /collection-in-carts` : Updates an existing collectionInCart.
     *
     * @param collectionInCart the collectionInCart to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated collectionInCart,
     * or with status `400 (Bad Request)` if the collectionInCart is not valid,
     * or with status `500 (Internal Server Error)` if the collectionInCart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/collection-in-carts")
    fun updateCollectionInCart(@RequestBody collectionInCart: CollectionInCart): ResponseEntity<CollectionInCart> {
        log.debug("REST request to update CollectionInCart : {}", collectionInCart)
        if (collectionInCart.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = collectionInCartService.save(collectionInCart)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     collectionInCart.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /collection-in-carts` : get all the collectionInCarts.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of collectionInCarts in body.
     */
    @GetMapping("/collection-in-carts")
    fun getAllCollectionInCarts(): MutableList<CollectionInCart> {
        log.debug("REST request to get all CollectionInCarts")
        return collectionInCartService.findAll()
    }

    /**
     * `GET  /collection-in-carts/:id` : get the "id" collectionInCart.
     *
     * @param id the id of the collectionInCart to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the collectionInCart, or with status `404 (Not Found)`.
     */
    @GetMapping("/collection-in-carts/{id}")
    fun getCollectionInCart(@PathVariable id: Long): ResponseEntity<CollectionInCart> {
        log.debug("REST request to get CollectionInCart : {}", id)
        val collectionInCart = collectionInCartService.findOne(id)
        return ResponseUtil.wrapOrNotFound(collectionInCart)
    }
    /**
     *  `DELETE  /collection-in-carts/:id` : delete the "id" collectionInCart.
     *
     * @param id the id of the collectionInCart to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/collection-in-carts/{id}")
    fun deleteCollectionInCart(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete CollectionInCart : {}", id)
        collectionInCartService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/collection-in-carts?query=:query` : search for the collectionInCart corresponding
     * to the query.
     *
     * @param query the query of the collectionInCart search.
     * @return the result of the search.
     */
    @GetMapping("/_search/collection-in-carts")
    fun searchCollectionInCarts(@RequestParam query: String): MutableList<CollectionInCart> {
        log.debug("REST request to search CollectionInCarts for query {}", query)
        return collectionInCartService.search(query).toMutableList()
    }
}
