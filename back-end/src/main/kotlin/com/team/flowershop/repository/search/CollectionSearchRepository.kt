package com.team.flowershop.repository.search

import com.team.flowershop.domain.Collection
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Collection] entity.
 */
interface CollectionSearchRepository : ElasticsearchRepository<Collection, Long>
