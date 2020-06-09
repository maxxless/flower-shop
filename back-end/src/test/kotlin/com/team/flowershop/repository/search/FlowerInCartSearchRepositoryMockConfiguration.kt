package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of FlowerInCartSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class FlowerInCartSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockFlowerInCartSearchRepository: FlowerInCartSearchRepository
}
