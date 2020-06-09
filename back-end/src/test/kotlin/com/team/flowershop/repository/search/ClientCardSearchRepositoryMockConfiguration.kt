package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of ClientCardSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class ClientCardSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockClientCardSearchRepository: ClientCardSearchRepository
}
