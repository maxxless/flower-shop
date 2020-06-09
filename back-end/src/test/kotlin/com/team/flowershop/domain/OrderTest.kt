package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Order::class)
        val order1 = Order()
        order1.id = 1L
        val order2 = Order()
        order2.id = order1.id
        assertThat(order1).isEqualTo(order2)
        order2.id = 2L
        assertThat(order1).isNotEqualTo(order2)
        order1.id = null
        assertThat(order1).isNotEqualTo(order2)
    }
}
