package com.team.flowershop.repository

import com.team.flowershop.domain.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Cart] entity.
 */
@Suppress("unused")
@Repository
interface CartRepository : JpaRepository<Cart, Long>
