package com.team.flowershop.repository.search

import com.team.flowershop.domain.Cart
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Cart] entity.
 */
interface CartSearchRepository : ElasticsearchRepository<Cart, Long>
