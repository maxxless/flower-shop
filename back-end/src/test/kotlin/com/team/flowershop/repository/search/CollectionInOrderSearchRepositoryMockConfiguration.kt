package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of CollectionInOrderSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class CollectionInOrderSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockCollectionInOrderSearchRepository: CollectionInOrderSearchRepository
}
