package com.team.flowershop.repository

import com.team.flowershop.domain.Collection
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Collection] entity.
 */
@Repository
interface CollectionRepository : JpaRepository<Collection, Long>, JpaSpecificationExecutor<Collection> {

    @Query(value = "select distinct collection from Collection collection left join fetch collection.availablePackings left join fetch collection.flowers",
        countQuery = "select count(distinct collection) from Collection collection")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Collection>

    @Query("select distinct collection from Collection collection left join fetch collection.availablePackings left join fetch collection.flowers")
    fun findAllWithEagerRelationships(): MutableList<Collection>

    @Query("select collection from Collection collection left join fetch collection.availablePackings left join fetch collection.flowers where collection.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Collection>
}
