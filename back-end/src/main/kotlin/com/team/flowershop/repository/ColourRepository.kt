package com.team.flowershop.repository

import com.team.flowershop.domain.Colour
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Colour] entity.
 */
@Suppress("unused")
@Repository
interface ColourRepository : JpaRepository<Colour, Long>
