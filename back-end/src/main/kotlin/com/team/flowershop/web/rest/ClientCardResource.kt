package com.team.flowershop.web.rest

import com.team.flowershop.domain.ClientCard
import com.team.flowershop.service.ClientCardService
import com.team.flowershop.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
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

private const val ENTITY_NAME = "clientCard"
/**
 * REST controller for managing [com.team.flowershop.domain.ClientCard].
 */
@RestController
@RequestMapping("/api")
class ClientCardResource(
    private val clientCardService: ClientCardService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /client-cards` : Create a new clientCard.
     *
     * @param clientCard the clientCard to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new clientCard, or with status `400 (Bad Request)` if the clientCard has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-cards")
    fun createClientCard(@Valid @RequestBody clientCard: ClientCard): ResponseEntity<ClientCard> {
        log.debug("REST request to save ClientCard : {}", clientCard)
        if (clientCard.id != null) {
            throw BadRequestAlertException(
                "A new clientCard cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        if (Objects.isNull(clientCard.user)) {
            throw BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null")
        }
        val result = clientCardService.save(clientCard)
        return ResponseEntity.created(URI("/api/client-cards/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /client-cards` : Updates an existing clientCard.
     *
     * @param clientCard the clientCard to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated clientCard,
     * or with status `400 (Bad Request)` if the clientCard is not valid,
     * or with status `500 (Internal Server Error)` if the clientCard couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-cards")
    fun updateClientCard(@Valid @RequestBody clientCard: ClientCard): ResponseEntity<ClientCard> {
        log.debug("REST request to update ClientCard : {}", clientCard)
        if (clientCard.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = clientCardService.save(clientCard)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     clientCard.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /client-cards` : get all the clientCards.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of clientCards in body.
     */
    @GetMapping("/client-cards")
    fun getAllClientCards(): MutableList<ClientCard> {
        log.debug("REST request to get all ClientCards")
        return clientCardService.findAll()
    }

    /**
     * `GET  /client-cards/:id` : get the "id" clientCard.
     *
     * @param id the id of the clientCard to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the clientCard, or with status `404 (Not Found)`.
     */
    @GetMapping("/client-cards/{id}")
    fun getClientCard(@PathVariable id: Long): ResponseEntity<ClientCard> {
        log.debug("REST request to get ClientCard : {}", id)
        val clientCard = clientCardService.findOne(id)
        return ResponseUtil.wrapOrNotFound(clientCard)
    }
    /**
     *  `DELETE  /client-cards/:id` : delete the "id" clientCard.
     *
     * @param id the id of the clientCard to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/client-cards/{id}")
    fun deleteClientCard(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ClientCard : {}", id)
        clientCardService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/client-cards?query=:query` : search for the clientCard corresponding
     * to the query.
     *
     * @param query the query of the clientCard search.
     * @return the result of the search.
     */
    @GetMapping("/_search/client-cards")
    fun searchClientCards(@RequestParam query: String): MutableList<ClientCard> {
        log.debug("REST request to search ClientCards for query {}", query)
        return clientCardService.search(query).toMutableList()
    }
}
