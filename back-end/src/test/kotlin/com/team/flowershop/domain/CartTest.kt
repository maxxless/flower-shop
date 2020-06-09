package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CartTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Cart::class)
        val cart1 = Cart()
        cart1.id = 1L
        val cart2 = Cart()
        cart2.id = cart1.id
        assertThat(cart1).isEqualTo(cart2)
        cart2.id = 2L
        assertThat(cart1).isNotEqualTo(cart2)
        cart1.id = null
        assertThat(cart1).isNotEqualTo(cart2)
    }
}
