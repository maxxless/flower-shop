package com.team.flowershop.service
import com.team.flowershop.domain.Category
import java.util.Optional

/**
 * Service Interface for managing [Category].
 */
interface CategoryService {

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    fun save(category: Category): Category

    /**
     * Get all the categories.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<Category>

    /**
     * Get the "id" category.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Category>

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)

    /**
     * Search for the category corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    fun search(query: String): MutableList<Category>
}
