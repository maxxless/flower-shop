package com.team.flowershop.web.rest

import com.team.flowershop.domain.Colour
import com.team.flowershop.service.ColourService
import com.team.flowershop.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
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

private const val ENTITY_NAME = "colour"
/**
 * REST controller for managing [com.team.flowershop.domain.Colour].
 */
@RestController
@RequestMapping("/api")
class ColourResource(
    private val colourService: ColourService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /colours` : Create a new colour.
     *
     * @param colour the colour to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new colour, or with status `400 (Bad Request)` if the colour has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/colours")
    fun createColour(@Valid @RequestBody colour: Colour): ResponseEntity<Colour> {
        log.debug("REST request to save Colour : {}", colour)
        if (colour.id != null) {
            throw BadRequestAlertException(
                "A new colour cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = colourService.save(colour)
        return ResponseEntity.created(URI("/api/colours/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /colours` : Updates an existing colour.
     *
     * @param colour the colour to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated colour,
     * or with status `400 (Bad Request)` if the colour is not valid,
     * or with status `500 (Internal Server Error)` if the colour couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/colours")
    fun updateColour(@Valid @RequestBody colour: Colour): ResponseEntity<Colour> {
        log.debug("REST request to update Colour : {}", colour)
        if (colour.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = colourService.save(colour)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     colour.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /colours` : get all the colours.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of colours in body.
     */
    @GetMapping("/colours")
    fun getAllColours(): MutableList<Colour> {
        log.debug("REST request to get all Colours")
        return colourService.findAll()
    }

    /**
     * `GET  /colours/:id` : get the "id" colour.
     *
     * @param id the id of the colour to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the colour, or with status `404 (Not Found)`.
     */
    @GetMapping("/colours/{id}")
    fun getColour(@PathVariable id: Long): ResponseEntity<Colour> {
        log.debug("REST request to get Colour : {}", id)
        val colour = colourService.findOne(id)
        return ResponseUtil.wrapOrNotFound(colour)
    }
    /**
     *  `DELETE  /colours/:id` : delete the "id" colour.
     *
     * @param id the id of the colour to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/colours/{id}")
    fun deleteColour(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Colour : {}", id)
        colourService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/colours?query=:query` : search for the colour corresponding
     * to the query.
     *
     * @param query the query of the colour search.
     * @return the result of the search.
     */
    @GetMapping("/_search/colours")
    fun searchColours(@RequestParam query: String): MutableList<Colour> {
        log.debug("REST request to search Colours for query {}", query)
        return colourService.search(query).toMutableList()
    }
}
