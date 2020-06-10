package com.team.flowershop.repository.search

import com.team.flowershop.domain.Category
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Category] entity.
 */
interface CategorySearchRepository : ElasticsearchRepository<Category, Long>
