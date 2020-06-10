package com.team.flowershop.repository

import com.team.flowershop.domain.FlowerInCart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [FlowerInCart] entity.
 */
@Suppress("unused")
@Repository
interface FlowerInCartRepository : JpaRepository<FlowerInCart, Long>
