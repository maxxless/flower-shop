package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of FlowerSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class FlowerSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockFlowerSearchRepository: FlowerSearchRepository
}
