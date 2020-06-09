package com.team.flowershop.domain

import com.team.flowershop.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClientCardTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ClientCard::class)
        val clientCard1 = ClientCard()
        clientCard1.id = 1L
        val clientCard2 = ClientCard()
        clientCard2.id = clientCard1.id
        assertThat(clientCard1).isEqualTo(clientCard2)
        clientCard2.id = 2L
        assertThat(clientCard1).isNotEqualTo(clientCard2)
        clientCard1.id = null
        assertThat(clientCard1).isNotEqualTo(clientCard2)
    }
}
