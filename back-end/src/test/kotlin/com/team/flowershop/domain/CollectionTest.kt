package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CollectionTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Collection::class)
        val collection1 = Collection()
        collection1.id = 1L
        val collection2 = Collection()
        collection2.id = collection1.id
        assertThat(collection1).isEqualTo(collection2)
        collection2.id = 2L
        assertThat(collection1).isNotEqualTo(collection2)
        collection1.id = null
        assertThat(collection1).isNotEqualTo(collection2)
    }
}
