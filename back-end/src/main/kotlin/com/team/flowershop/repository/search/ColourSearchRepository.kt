package com.team.flowershop.repository.search

import com.team.flowershop.domain.Colour
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Colour] entity.
 */
interface ColourSearchRepository : ElasticsearchRepository<Colour, Long>
