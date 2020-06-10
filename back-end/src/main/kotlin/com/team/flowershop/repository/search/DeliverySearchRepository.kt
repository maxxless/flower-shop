package com.team.flowershop.repository.search

import com.team.flowershop.domain.Delivery
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Delivery] entity.
 */
interface DeliverySearchRepository : ElasticsearchRepository<Delivery, Long>
