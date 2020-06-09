package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of PackingSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class PackingSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockPackingSearchRepository: PackingSearchRepository
}
