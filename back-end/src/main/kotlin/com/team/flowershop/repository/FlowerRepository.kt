package com.team.flowershop.repository

import com.team.flowershop.domain.Flower
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Flower] entity.
 */
@Repository
interface FlowerRepository : JpaRepository<Flower, Long>, JpaSpecificationExecutor<Flower> {

    @Query(value = "select distinct flower from Flower flower left join fetch flower.availableColours",
        countQuery = "select count(distinct flower) from Flower flower")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Flower>

    @Query("select distinct flower from Flower flower left join fetch flower.availableColours")
    fun findAllWithEagerRelationships(): MutableList<Flower>

    @Query("select flower from Flower flower left join fetch flower.availableColours where flower.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Flower>
}
