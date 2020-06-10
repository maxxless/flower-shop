package com.team.flowershop.service.impl

import com.team.flowershop.domain.Colour
import com.team.flowershop.repository.ColourRepository
import com.team.flowershop.repository.search.ColourSearchRepository
import com.team.flowershop.service.ColourService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Colour].
 */
@Service
@Transactional
class ColourServiceImpl(
    private val colourRepository: ColourRepository,
    private val colourSearchRepository: ColourSearchRepository
) : ColourService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a colour.
     *
     * @param colour the entity to save.
     * @return the persisted entity.
     */
    override fun save(colour: Colour): Colour {
        log.debug("Request to save Colour : {}", colour)
        val result = colourRepository.save(colour)
        colourSearchRepository.save(result)
        return result
    }

    /**
     * Get all the colours.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<Colour> {
        log.debug("Request to get all Colours")
        return colourRepository.findAll()
    }

    /**
     * Get one colour by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Colour> {
        log.debug("Request to get Colour : {}", id)
        return colourRepository.findById(id)
    }

    /**
     * Delete the colour by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete Colour : {}", id)

        colourRepository.deleteById(id)
        colourSearchRepository.deleteById(id)
    }

    /**
     * Search for the colour corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<Colour> {
        log.debug("Request to search Colours for query {}", query)
        return colourSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
