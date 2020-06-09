package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColourTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Colour::class)
        val colour1 = Colour()
        colour1.id = 1L
        val colour2 = Colour()
        colour2.id = colour1.id
        assertThat(colour1).isEqualTo(colour2)
        colour2.id = 2L
        assertThat(colour1).isNotEqualTo(colour2)
        colour1.id = null
        assertThat(colour1).isNotEqualTo(colour2)
    }
}
