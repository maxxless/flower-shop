package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of CollectionSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class CollectionSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockCollectionSearchRepository: CollectionSearchRepository
}
