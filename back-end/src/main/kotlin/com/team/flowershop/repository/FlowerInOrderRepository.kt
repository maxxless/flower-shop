package com.team.flowershop.repository

import com.team.flowershop.domain.FlowerInOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [FlowerInOrder] entity.
 */
@Suppress("unused")
@Repository
interface FlowerInOrderRepository : JpaRepository<FlowerInOrder, Long>
