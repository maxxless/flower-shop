package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeliveryTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Delivery::class)
        val delivery1 = Delivery()
        delivery1.id = 1L
        val delivery2 = Delivery()
        delivery2.id = delivery1.id
        assertThat(delivery1).isEqualTo(delivery2)
        delivery2.id = 2L
        assertThat(delivery1).isNotEqualTo(delivery2)
        delivery1.id = null
        assertThat(delivery1).isNotEqualTo(delivery2)
    }
}
