package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of OrderSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class OrderSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockOrderSearchRepository: OrderSearchRepository
}
