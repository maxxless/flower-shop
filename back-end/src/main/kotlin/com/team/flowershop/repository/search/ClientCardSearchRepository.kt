package com.team.flowershop.repository.search

import com.team.flowershop.domain.ClientCard
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [ClientCard] entity.
 */
interface ClientCardSearchRepository : ElasticsearchRepository<ClientCard, Long>
