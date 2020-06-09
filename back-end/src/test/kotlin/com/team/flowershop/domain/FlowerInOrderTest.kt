package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FlowerInOrderTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(FlowerInOrder::class)
        val flowerInOrder1 = FlowerInOrder()
        flowerInOrder1.id = 1L
        val flowerInOrder2 = FlowerInOrder()
        flowerInOrder2.id = flowerInOrder1.id
        assertThat(flowerInOrder1).isEqualTo(flowerInOrder2)
        flowerInOrder2.id = 2L
        assertThat(flowerInOrder1).isNotEqualTo(flowerInOrder2)
        flowerInOrder1.id = null
        assertThat(flowerInOrder1).isNotEqualTo(flowerInOrder2)
    }
}
