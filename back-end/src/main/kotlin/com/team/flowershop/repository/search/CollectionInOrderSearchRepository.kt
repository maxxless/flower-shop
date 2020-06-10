package com.team.flowershop.repository.search

import com.team.flowershop.domain.CollectionInOrder
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [CollectionInOrder] entity.
 */
interface CollectionInOrderSearchRepository : ElasticsearchRepository<CollectionInOrder, Long>
