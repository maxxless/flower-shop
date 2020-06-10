package com.team.flowershop.repository.search

import com.team.flowershop.domain.Packing
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Packing] entity.
 */
interface PackingSearchRepository : ElasticsearchRepository<Packing, Long>
