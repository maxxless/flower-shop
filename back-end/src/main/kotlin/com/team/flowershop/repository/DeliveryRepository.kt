package com.team.flowershop.repository

import com.team.flowershop.domain.Delivery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Delivery] entity.
 */
@Suppress("unused")
@Repository
interface DeliveryRepository : JpaRepository<Delivery, Long> {

    @Query("select delivery from Delivery delivery where delivery.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Delivery>
}
