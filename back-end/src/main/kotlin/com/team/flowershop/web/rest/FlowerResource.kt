package com.team.flowershop.web.rest

import com.team.flowershop.domain.Flower
import com.team.flowershop.service.FlowerQueryService
import com.team.flowershop.service.FlowerService
import com.team.flowershop.service.dto.FlowerCriteria
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

private const val ENTITY_NAME = "flower"
/**
 * REST controller for managing [com.team.flowershop.domain.Flower].
 */
@RestController
@RequestMapping("/api")
class FlowerResource(
    private val flowerService: FlowerService,
    private val flowerQueryService: FlowerQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /flowers` : Create a new flower.
     *
     * @param flower the flower to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new flower, or with status `400 (Bad Request)` if the flower has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/flowers")
    fun createFlower(@Valid @RequestBody flower: Flower): ResponseEntity<Flower> {
        log.debug("REST request to save Flower : {}", flower)
        if (flower.id != null) {
            throw BadRequestAlertException(
                "A new flower cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = flowerService.save(flower)
        return ResponseEntity.created(URI("/api/flowers/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /flowers` : Updates an existing flower.
     *
     * @param flower the flower to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated flower,
     * or with status `400 (Bad Request)` if the flower is not valid,
     * or with status `500 (Internal Server Error)` if the flower couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/flowers")
    fun updateFlower(@Valid @RequestBody flower: Flower): ResponseEntity<Flower> {
        log.debug("REST request to update Flower : {}", flower)
        if (flower.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = flowerService.save(flower)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     flower.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /flowers` : get all the flowers.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of flowers in body.
     */
    @GetMapping("/flowers") fun getAllFlowers(
        criteria: FlowerCriteria,
        pageable: Pageable

    ): ResponseEntity<MutableList<Flower>> {
        log.debug("REST request to get Flowers by criteria: {}", criteria)
        val page = flowerQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /flowers/count}` : count all the flowers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/flowers/count")
    fun countFlowers(criteria: FlowerCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Flowers by criteria: {}", criteria)
        return ResponseEntity.ok().body(flowerQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /flowers/:id` : get the "id" flower.
     *
     * @param id the id of the flower to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the flower, or with status `404 (Not Found)`.
     */
    @GetMapping("/flowers/{id}")
    fun getFlower(@PathVariable id: Long): ResponseEntity<Flower> {
        log.debug("REST request to get Flower : {}", id)
        val flower = flowerService.findOne(id)
        return ResponseUtil.wrapOrNotFound(flower)
    }
    /**
     *  `DELETE  /flowers/:id` : delete the "id" flower.
     *
     * @param id the id of the flower to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/flowers/{id}")
    fun deleteFlower(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Flower : {}", id)
        flowerService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/flowers?query=:query` : search for the flower corresponding
     * to the query.
     *
     * @param query the query of the flower search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/flowers")
    fun searchFlowers(@RequestParam query: String, pageable: Pageable): ResponseEntity<MutableList<Flower>> {
        log.debug("REST request to search for a page of Flowers for query {}", query)
        val page = flowerService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }
}
