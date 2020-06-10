package com.team.flowershop.repository.search

import com.team.flowershop.domain.Order
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Order] entity.
 */
interface OrderSearchRepository : ElasticsearchRepository<Order, Long>
