package com.team.flowershop.web.rest

import com.team.flowershop.domain.Collection
import com.team.flowershop.service.CollectionQueryService
import com.team.flowershop.service.CollectionService
import com.team.flowershop.service.dto.CollectionCriteria
import com.team.flowershop.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
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

private const val ENTITY_NAME = "collection"
/**
 * REST controller for managing [com.team.flowershop.domain.Collection].
 */
@RestController
@RequestMapping("/api")
class CollectionResource(
    private val collectionService: CollectionService,
    private val collectionQueryService: CollectionQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /collections` : Create a new collection.
     *
     * @param collection the collection to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new collection, or with status `400 (Bad Request)` if the collection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/collections")
    fun createCollection(@Valid @RequestBody collection: Collection): ResponseEntity<Collection> {
        log.debug("REST request to save Collection : {}", collection)
        if (collection.id != null) {
            throw BadRequestAlertException(
                "A new collection cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = collectionService.save(collection)
        return ResponseEntity.created(URI("/api/collections/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /collections` : Updates an existing collection.
     *
     * @param collection the collection to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated collection,
     * or with status `400 (Bad Request)` if the collection is not valid,
     * or with status `500 (Internal Server Error)` if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/collections")
    fun updateCollection(@Valid @RequestBody collection: Collection): ResponseEntity<Collection> {
        log.debug("REST request to update Collection : {}", collection)
        if (collection.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = collectionService.save(collection)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     collection.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /collections` : get all the collections.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of collections in body.
     */
    @GetMapping("/collections") fun getAllCollections(
        criteria: CollectionCriteria,
        pageable: Pageable

    ): ResponseEntity<MutableList<Collection>> {
        log.debug("REST request to get Collections by criteria: {}", criteria)
        val page = collectionQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /collections/count}` : count all the collections.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/collections/count")
    fun countCollections(criteria: CollectionCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Collections by criteria: {}", criteria)
        return ResponseEntity.ok().body(collectionQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /collections/:id` : get the "id" collection.
     *
     * @param id the id of the collection to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the collection, or with status `404 (Not Found)`.
     */
    @GetMapping("/collections/{id}")
    fun getCollection(@PathVariable id: Long): ResponseEntity<Collection> {
        log.debug("REST request to get Collection : {}", id)
        val collection = collectionService.findOne(id)
        return ResponseUtil.wrapOrNotFound(collection)
    }
    /**
     *  `DELETE  /collections/:id` : delete the "id" collection.
     *
     * @param id the id of the collection to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/collections/{id}")
    fun deleteCollection(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Collection : {}", id)
        collectionService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/collections?query=:query` : search for the collection corresponding
     * to the query.
     *
     * @param query the query of the collection search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/collections")
    fun searchCollections(@RequestParam query: String, pageable: Pageable): ResponseEntity<MutableList<Collection>> {
        log.debug("REST request to search for a page of Collections for query {}", query)
        val page = collectionService.search("$query*", pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }
}
