package com.team.flowershop.repository.search

import com.team.flowershop.domain.FlowerInCart
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [FlowerInCart] entity.
 */
interface FlowerInCartSearchRepository : ElasticsearchRepository<FlowerInCart, Long>
