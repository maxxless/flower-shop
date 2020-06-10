package com.team.flowershop.repository

import com.team.flowershop.domain.CollectionInOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [CollectionInOrder] entity.
 */
@Suppress("unused")
@Repository
interface CollectionInOrderRepository : JpaRepository<CollectionInOrder, Long>
