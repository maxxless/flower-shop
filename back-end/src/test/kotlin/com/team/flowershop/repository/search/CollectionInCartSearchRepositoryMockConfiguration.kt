package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of CollectionInCartSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class CollectionInCartSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockCollectionInCartSearchRepository: CollectionInCartSearchRepository
}
