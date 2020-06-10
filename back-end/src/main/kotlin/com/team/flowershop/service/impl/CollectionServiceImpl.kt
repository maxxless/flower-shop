package com.team.flowershop.service.impl

import com.team.flowershop.domain.Collection
import com.team.flowershop.repository.CollectionRepository
import com.team.flowershop.repository.search.CollectionSearchRepository
import com.team.flowershop.service.CollectionService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Collection].
 */
@Service
@Transactional
class CollectionServiceImpl(
    private val collectionRepository: CollectionRepository,
    private val collectionSearchRepository: CollectionSearchRepository
) : CollectionService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a collection.
     *
     * @param collection the entity to save.
     * @return the persisted entity.
     */
    override fun save(collection: Collection): Collection {
        log.debug("Request to save Collection : {}", collection)
        val result = collectionRepository.save(collection)
        collectionSearchRepository.save(result)
        return result
    }

    /**
     * Get all the collections.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Collection> {
        log.debug("Request to get all Collections")
        return collectionRepository.findAll(pageable)
    }

    /**
     * Get all the collections with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    override fun findAllWithEagerRelationships(pageable: Pageable) =
        collectionRepository.findAllWithEagerRelationships(pageable)

    /**
     * Get one collection by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Collection> {
        log.debug("Request to get Collection : {}", id)
        return collectionRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the collection by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Collection : {}", id)

        collectionRepository.deleteById(id)
        collectionSearchRepository.deleteById(id)
    }

    /**
     * Search for the collection corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String, pageable: Pageable): Page<Collection> {
        log.debug("Request to search for a page of Collections for query {}", query)
        return collectionSearchRepository.search(queryStringQuery(query), pageable)
    }
}
