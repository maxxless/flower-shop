package com.team.flowershop.service
import com.team.flowershop.domain.Colour
import java.util.Optional

/**
 * Service Interface for managing [Colour].
 */
interface ColourService {

    /**
     * Save a colour.
     *
     * @param colour the entity to save.
     * @return the persisted entity.
     */
    fun save(colour: Colour): Colour

    /**
     * Get all the colours.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<Colour>

    /**
     * Get the "id" colour.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Colour>

    /**
     * Delete the "id" colour.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the colour corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<Colour>
}
