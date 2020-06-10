package com.team.flowershop.repository

import com.team.flowershop.domain.CollectionInCart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [CollectionInCart] entity.
 */
@Suppress("unused")
@Repository
interface CollectionInCartRepository : JpaRepository<CollectionInCart, Long>
