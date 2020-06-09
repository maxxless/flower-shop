package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PackingTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Packing::class)
        val packing1 = Packing()
        packing1.id = 1L
        val packing2 = Packing()
        packing2.id = packing1.id
        assertThat(packing1).isEqualTo(packing2)
        packing2.id = 2L
        assertThat(packing1).isNotEqualTo(packing2)
        packing1.id = null
        assertThat(packing1).isNotEqualTo(packing2)
    }
}
