package com.team.flowershop.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of ColourSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class ColourSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockColourSearchRepository: ColourSearchRepository
}
