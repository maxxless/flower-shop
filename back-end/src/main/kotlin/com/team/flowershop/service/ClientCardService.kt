package com.team.flowershop.service
import com.team.flowershop.domain.ClientCard
import java.util.Optional

/**
 * Service Interface for managing [ClientCard].
 */
interface ClientCardService {

    /**
     * Save a clientCard.
     *
     * @param clientCard the entity to save.
     * @return the persisted entity.
     */
    fun save(clientCard: ClientCard): ClientCard

    /**
     * Get all the clientCards.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<ClientCard>

    /**
     * Get the "id" clientCard.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<ClientCard>

    /**
     * Delete the "id" clientCard.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the clientCard corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<ClientCard>
}
