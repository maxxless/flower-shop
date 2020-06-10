package com.team.flowershop.repository

import com.team.flowershop.domain.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Order] entity.
 */
@Suppress("unused")
@Repository
interface OrderRepository : JpaRepository<Order, Long> {

    @Query("select order from Order order where order.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Order>
}
