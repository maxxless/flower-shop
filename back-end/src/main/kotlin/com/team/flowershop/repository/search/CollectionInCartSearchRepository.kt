package com.team.flowershop.repository.search

import com.team.flowershop.domain.CollectionInCart
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [CollectionInCart] entity.
 */
interface CollectionInCartSearchRepository : ElasticsearchRepository<CollectionInCart, Long>
