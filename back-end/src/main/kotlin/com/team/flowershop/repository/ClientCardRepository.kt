package com.team.flowershop.repository

import com.team.flowershop.domain.ClientCard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ClientCard] entity.
 */
@Suppress("unused")
@Repository
interface ClientCardRepository : JpaRepository<ClientCard, Long>
