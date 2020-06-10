package com.team.flowershop.service.impl

import com.team.flowershop.domain.Category
import com.team.flowershop.repository.CategoryRepository
import com.team.flowershop.repository.search.CategorySearchRepository
import com.team.flowershop.service.CategoryService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Category].
 */
@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val categorySearchRepository: CategorySearchRepository
) : CategoryService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    override fun save(category: Category): Category {
        log.debug("Request to save Category : {}", category)
        val result = categoryRepository.save(category)
        categorySearchRepository.save(result)
        return result
    }

    /**
     * Get all the categories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<Category> {
        log.debug("Request to get all Categories")
        return categoryRepository.findAll()
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Category> {
        log.debug("Request to get Category : {}", id)
        return categoryRepository.findById(id)
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Category : {}", id)

        categoryRepository.deleteById(id)
        categorySearchRepository.deleteById(id)
    }

    /**
     * Search for the category corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<Category> {
        log.debug("Request to search Categories for query {}", query)
        return categorySearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
