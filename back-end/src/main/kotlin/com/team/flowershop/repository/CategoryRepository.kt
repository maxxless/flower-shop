package com.team.flowershop.repository

import com.team.flowershop.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Category] entity.
 */
@Suppress("unused")
@Repository
interface CategoryRepository : JpaRepository<Category, Long>
