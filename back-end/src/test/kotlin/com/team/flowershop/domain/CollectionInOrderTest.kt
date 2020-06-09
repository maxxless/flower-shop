package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CollectionInOrderTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(CollectionInOrder::class)
        val collectionInOrder1 = CollectionInOrder()
        collectionInOrder1.id = 1L
        val collectionInOrder2 = CollectionInOrder()
        collectionInOrder2.id = collectionInOrder1.id
        assertThat(collectionInOrder1).isEqualTo(collectionInOrder2)
        collectionInOrder2.id = 2L
        assertThat(collectionInOrder1).isNotEqualTo(collectionInOrder2)
        collectionInOrder1.id = null
        assertThat(collectionInOrder1).isNotEqualTo(collectionInOrder2)
    }
}
