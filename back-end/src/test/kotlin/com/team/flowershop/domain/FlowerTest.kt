package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FlowerTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Flower::class)
        val flower1 = Flower()
        flower1.id = 1L
        val flower2 = Flower()
        flower2.id = flower1.id
        assertThat(flower1).isEqualTo(flower2)
        flower2.id = 2L
        assertThat(flower1).isNotEqualTo(flower2)
        flower1.id = null
        assertThat(flower1).isNotEqualTo(flower2)
    }
}
