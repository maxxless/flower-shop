package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CollectionInCartTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(CollectionInCart::class)
        val collectionInCart1 = CollectionInCart()
        collectionInCart1.id = 1L
        val collectionInCart2 = CollectionInCart()
        collectionInCart2.id = collectionInCart1.id
        assertThat(collectionInCart1).isEqualTo(collectionInCart2)
        collectionInCart2.id = 2L
        assertThat(collectionInCart1).isNotEqualTo(collectionInCart2)
        collectionInCart1.id = null
        assertThat(collectionInCart1).isNotEqualTo(collectionInCart2)
    }
}
