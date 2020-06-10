package com.team.flowershop.service.impl

import com.team.flowershop.domain.ClientCard
import com.team.flowershop.repository.ClientCardRepository
import com.team.flowershop.repository.UserRepository
import com.team.flowershop.repository.search.ClientCardSearchRepository
import com.team.flowershop.service.ClientCardService
import java.util.Optional
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [ClientCard].
 */
@Service
@Transactional
class ClientCardServiceImpl(
    private val clientCardRepository: ClientCardRepository,
    private val clientCardSearchRepository: ClientCardSearchRepository,
    private val userRepository: UserRepository
) : ClientCardService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a clientCard.
     *
     * @param clientCard the entity to save.
     * @return the persisted entity.
     */
    override fun save(clientCard: ClientCard): ClientCard {
        log.debug("Request to save ClientCard : {}", clientCard)
        val userId = clientCard.user?.id
        if (userId != null) {
            userRepository.findById(userId)
                .ifPresent { clientCard.user = it }
        }
        val result = clientCardRepository.save(clientCard)
        clientCardSearchRepository.save(result)
        return result
    }

    /**
     * Get all the clientCards.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<ClientCard> {
        log.debug("Request to get all ClientCards")
        return clientCardRepository.findAll()
    }

    /**
     * Get one clientCard by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<ClientCard> {
        log.debug("Request to get ClientCard : {}", id)
        return clientCardRepository.findById(id)
    }

    /**
     * Delete the clientCard by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete ClientCard : {}", id)

        clientCardRepository.deleteById(id)
        clientCardSearchRepository.deleteById(id)
    }

    /**
     * Search for the clientCard corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun search(query: String): MutableList<ClientCard> {
        log.debug("Request to search ClientCards for query {}", query)
        return clientCardSearchRepository.search(queryStringQuery(query))
            .toMutableList()
    }
}
