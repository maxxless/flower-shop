package com.team.flowershop.repository.search

import com.team.flowershop.domain.Flower
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Flower] entity.
 */
interface FlowerSearchRepository : ElasticsearchRepository<Flower, Long>
