package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of FlowerInOrderSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class FlowerInOrderSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockFlowerInOrderSearchRepository: FlowerInOrderSearchRepository
}
