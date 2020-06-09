package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FlowerInCartTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(FlowerInCart::class)
        val flowerInCart1 = FlowerInCart()
        flowerInCart1.id = 1L
        val flowerInCart2 = FlowerInCart()
        flowerInCart2.id = flowerInCart1.id
        assertThat(flowerInCart1).isEqualTo(flowerInCart2)
        flowerInCart2.id = 2L
        assertThat(flowerInCart1).isNotEqualTo(flowerInCart2)
        flowerInCart1.id = null
        assertThat(flowerInCart1).isNotEqualTo(flowerInCart2)
    }
}
