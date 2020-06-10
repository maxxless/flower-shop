package com.team.flowershop.repository

import com.team.flowershop.domain.Packing
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Packing] entity.
 */
@Suppress("unused")
@Repository
interface PackingRepository : JpaRepository<Packing, Long>, JpaSpecificationExecutor<Packing>
