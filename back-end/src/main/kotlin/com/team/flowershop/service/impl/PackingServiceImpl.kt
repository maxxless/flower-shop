package com.team.flowershop.service.impl

import com.team.flowershop.domain.Packing
import com.team.flowershop.repository.PackingRepository
import com.team.flowershop.repository.search.PackingSearchRepository
import com.team.flowershop.service.PackingService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Packing].
 */
@Service
@Transactional
class PackingServiceImpl(
    private val packingRepository: PackingRepository,
    private val packingSearchRepository: PackingSearchRepository
) : PackingService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a packing.
     *
     * @param packing the entity to save.
     * @return the persisted entity.
     */
    override fun save(packing: Packing): Packing {
        log.debug("Request to save Packing : {}", packing)
        val result = packingRepository.save(packing)
        packingSearchRepository.save(result)
        return result
    }

    /**
     * Get all the packings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Packing> {
        log.debug("Request to get all Packings")
        return packingRepository.findAll(pageable)
    }

    /**
     * Get one packing by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Packing> {
        log.debug("Request to get Packing : {}", id)
        return packingRepository.findById(id)
    }

    /**
     * Delete the packing by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Packing : {}", id)

        packingRepository.deleteById(id)
        packingSearchRepository.deleteById(id)
    }

    /**
     * Search for the packing corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String, pageable: Pageable): Page<Packing> {
        log.debug("Request to search for a page of Packings for query {}", query)
        return packingSearchRepository.search(queryStringQuery(query), pageable)
    }
}
