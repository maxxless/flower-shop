package com.team.flowershop.web.rest

import com.team.flowershop.domain.Packing
import com.team.flowershop.service.PackingQueryService
import com.team.flowershop.service.PackingService
import com.team.flowershop.service.dto.PackingCriteria
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

private const val ENTITY_NAME = "packing"
/**
 * REST controller for managing [com.team.flowershop.domain.Packing].
 */
@RestController
@RequestMapping("/api")
class PackingResource(
    private val packingService: PackingService,
    private val packingQueryService: PackingQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /packings` : Create a new packing.
     *
     * @param packing the packing to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new packing, or with status `400 (Bad Request)` if the packing has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/packings")
    fun createPacking(@Valid @RequestBody packing: Packing): ResponseEntity<Packing> {
        log.debug("REST request to save Packing : {}", packing)
        if (packing.id != null) {
            throw BadRequestAlertException(
                "A new packing cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = packingService.save(packing)
        return ResponseEntity.created(URI("/api/packings/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /packings` : Updates an existing packing.
     *
     * @param packing the packing to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated packing,
     * or with status `400 (Bad Request)` if the packing is not valid,
     * or with status `500 (Internal Server Error)` if the packing couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/packings")
    fun updatePacking(@Valid @RequestBody packing: Packing): ResponseEntity<Packing> {
        log.debug("REST request to update Packing : {}", packing)
        if (packing.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = packingService.save(packing)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     packing.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /packings` : get all the packings.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of packings in body.
     */
    @GetMapping("/packings") fun getAllPackings(
        criteria: PackingCriteria,
        pageable: Pageable

    ): ResponseEntity<MutableList<Packing>> {
        log.debug("REST request to get Packings by criteria: {}", criteria)
        val page = packingQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /packings/count}` : count all the packings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/packings/count")
    fun countPackings(criteria: PackingCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Packings by criteria: {}", criteria)
        return ResponseEntity.ok().body(packingQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /packings/:id` : get the "id" packing.
     *
     * @param id the id of the packing to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the packing, or with status `404 (Not Found)`.
     */
    @GetMapping("/packings/{id}")
    fun getPacking(@PathVariable id: Long): ResponseEntity<Packing> {
        log.debug("REST request to get Packing : {}", id)
        val packing = packingService.findOne(id)
        return ResponseUtil.wrapOrNotFound(packing)
    }
    /**
     *  `DELETE  /packings/:id` : delete the "id" packing.
     *
     * @param id the id of the packing to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/packings/{id}")
    fun deletePacking(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Packing : {}", id)
        packingService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/packings?query=:query` : search for the packing corresponding
     * to the query.
     *
     * @param query the query of the packing search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/packings")
    fun searchPackings(@RequestParam query: String, pageable: Pageable): ResponseEntity<MutableList<Packing>> {
        log.debug("REST request to search for a page of Packings for query {}", query)
        val page = packingService.search("$query*", pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }
}
