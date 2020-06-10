package com.team.flowershop.repository.search

import com.team.flowershop.domain.FlowerInOrder
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [FlowerInOrder] entity.
 */
interface FlowerInOrderSearchRepository : ElasticsearchRepository<FlowerInOrder, Long>
