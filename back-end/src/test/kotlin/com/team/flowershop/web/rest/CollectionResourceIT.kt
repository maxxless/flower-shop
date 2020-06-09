package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Collection
import com.team.flowershop.repository.CollectionRepository
import com.team.flowershop.repository.search.CollectionSearchRepository
import com.team.flowershop.service.CollectionQueryService
import com.team.flowershop.service.CollectionService
import com.team.flowershop.web.rest.errors.ExceptionTranslator
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Base64Utils
import org.springframework.validation.Validator

/**
 * Integration tests for the [CollectionResource] REST controller.
 *
 * @see CollectionResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class CollectionResourceIT {

    @Autowired
    private lateinit var collectionRepository: CollectionRepository

    @Mock
    private lateinit var collectionRepositoryMock: CollectionRepository

    @Mock
    private lateinit var collectionServiceMock: CollectionService

    @Autowired
    private lateinit var collectionService: CollectionService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.CollectionSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockCollectionSearchRepository: CollectionSearchRepository

    @Autowired
    private lateinit var collectionQueryService: CollectionQueryService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restCollectionMockMvc: MockMvc

    private lateinit var collection: Collection

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val collectionResource = CollectionResource(collectionService, collectionQueryService)
        this.restCollectionMockMvc = MockMvcBuilders.standaloneSetup(collectionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        collection = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCollection() {
        val databaseSizeBeforeCreate = collectionRepository.findAll().size

        // Create the Collection
        restCollectionMockMvc.perform(
            post("/api/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collection))
        ).andExpect(status().isCreated)

        // Validate the Collection in the database
        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate + 1)
        val testCollection = collectionList[collectionList.size - 1]
        assertThat(testCollection.name).isEqualTo(DEFAULT_NAME)
        assertThat(testCollection.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testCollection.price).isEqualTo(DEFAULT_PRICE)
        assertThat(testCollection.image).isEqualTo(DEFAULT_IMAGE)
        assertThat(testCollection.imageContentType).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE)

        // Validate the Collection in Elasticsearch
        verify(mockCollectionSearchRepository, times(1)).save(testCollection)
    }

    @Test
    @Transactional
    fun createCollectionWithExistingId() {
        val databaseSizeBeforeCreate = collectionRepository.findAll().size

        // Create the Collection with an existing ID
        collection.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionMockMvc.perform(
            post("/api/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collection))
        ).andExpect(status().isBadRequest)

        // Validate the Collection in the database
        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate)

        // Validate the Collection in Elasticsearch
        verify(mockCollectionSearchRepository, times(0)).save(collection)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = collectionRepository.findAll().size
        // set the field null
        collection.name = null

        // Create the Collection, which fails.

        restCollectionMockMvc.perform(
            post("/api/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collection))
        ).andExpect(status().isBadRequest)

        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceIsRequired() {
        val databaseSizeBeforeTest = collectionRepository.findAll().size
        // set the field null
        collection.price = null

        // Create the Collection, which fails.

        restCollectionMockMvc.perform(
            post("/api/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collection))
        ).andExpect(status().isBadRequest)

        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllCollections() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList
        restCollectionMockMvc.perform(get("/api/collections?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
    }

    @Suppress("unchecked")
    fun getAllCollectionsWithEagerRelationshipsIsEnabled() {
        val collectionResource = CollectionResource(collectionServiceMock, collectionQueryService)
        `when`(collectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restCollectionMockMvc = MockMvcBuilders.standaloneSetup(collectionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restCollectionMockMvc.perform(get("/api/collections?eagerload=true"))
            .andExpect(status().isOk)

        verify(collectionServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    fun getAllCollectionsWithEagerRelationshipsIsNotEnabled() {
        val collectionResource = CollectionResource(collectionServiceMock, collectionQueryService)
            `when`(collectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restCollectionMockMvc = MockMvcBuilders.standaloneSetup(collectionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restCollectionMockMvc.perform(get("/api/collections?eagerload=true"))
            .andExpect(status().isOk)

        verify(collectionServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getCollection() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        val id = collection.id
        assertNotNull(id)

        // Get the collection
        restCollectionMockMvc.perform(get("/api/collections/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
    }

    @Test
    @Transactional
    fun getCollectionsByIdFiltering() {
      // Initialize the database
      collectionRepository.saveAndFlush(collection)
      val id = collection.id

      defaultCollectionShouldBeFound("id.equals=" + id)
      defaultCollectionShouldNotBeFound("id.notEquals=" + id)

      defaultCollectionShouldBeFound("id.greaterThanOrEqual=" + id)
      defaultCollectionShouldNotBeFound("id.greaterThan=" + id)

      defaultCollectionShouldBeFound("id.lessThanOrEqual=" + id)
      defaultCollectionShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    fun getAllCollectionsByNameIsEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where name equals to DEFAULT_NAME
        defaultCollectionShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the collectionList where name equals to UPDATED_NAME
        defaultCollectionShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByNameIsNotEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where name not equals to DEFAULT_NAME
        defaultCollectionShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the collectionList where name not equals to UPDATED_NAME
        defaultCollectionShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    fun getAllCollectionsByNameIsInShouldWork() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCollectionShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the collectionList where name equals to UPDATED_NAME
        defaultCollectionShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByNameIsNullOrNotNull() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where name is not null
        defaultCollectionShouldBeFound("name.specified=true")

        // Get all the collectionList where name is null
        defaultCollectionShouldNotBeFound("name.specified=false")
    }
                @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByNameContainsSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where name contains DEFAULT_NAME
        defaultCollectionShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the collectionList where name contains UPDATED_NAME
        defaultCollectionShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByNameNotContainsSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where name does not contain DEFAULT_NAME
        defaultCollectionShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the collectionList where name does not contain UPDATED_NAME
        defaultCollectionShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    fun getAllCollectionsByDescriptionIsEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where description equals to DEFAULT_DESCRIPTION
        defaultCollectionShouldBeFound("description.equals=$DEFAULT_DESCRIPTION")

        // Get all the collectionList where description equals to UPDATED_DESCRIPTION
        defaultCollectionShouldNotBeFound("description.equals=$UPDATED_DESCRIPTION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByDescriptionIsNotEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where description not equals to DEFAULT_DESCRIPTION
        defaultCollectionShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION)

        // Get all the collectionList where description not equals to UPDATED_DESCRIPTION
        defaultCollectionShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun getAllCollectionsByDescriptionIsInShouldWork() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCollectionShouldBeFound("description.in=$DEFAULT_DESCRIPTION,$UPDATED_DESCRIPTION")

        // Get all the collectionList where description equals to UPDATED_DESCRIPTION
        defaultCollectionShouldNotBeFound("description.in=$UPDATED_DESCRIPTION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where description is not null
        defaultCollectionShouldBeFound("description.specified=true")

        // Get all the collectionList where description is null
        defaultCollectionShouldNotBeFound("description.specified=false")
    }
                @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByDescriptionContainsSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where description contains DEFAULT_DESCRIPTION
        defaultCollectionShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION)

        // Get all the collectionList where description contains UPDATED_DESCRIPTION
        defaultCollectionShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByDescriptionNotContainsSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where description does not contain DEFAULT_DESCRIPTION
        defaultCollectionShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION)

        // Get all the collectionList where description does not contain UPDATED_DESCRIPTION
        defaultCollectionShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun getAllCollectionsByPriceIsEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price equals to DEFAULT_PRICE
        defaultCollectionShouldBeFound("price.equals=$DEFAULT_PRICE")

        // Get all the collectionList where price equals to UPDATED_PRICE
        defaultCollectionShouldNotBeFound("price.equals=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByPriceIsNotEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price not equals to DEFAULT_PRICE
        defaultCollectionShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE)

        // Get all the collectionList where price not equals to UPDATED_PRICE
        defaultCollectionShouldBeFound("price.notEquals=" + UPDATED_PRICE)
    }

    @Test
    @Transactional
    fun getAllCollectionsByPriceIsInShouldWork() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultCollectionShouldBeFound("price.in=$DEFAULT_PRICE,$UPDATED_PRICE")

        // Get all the collectionList where price equals to UPDATED_PRICE
        defaultCollectionShouldNotBeFound("price.in=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByPriceIsNullOrNotNull() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price is not null
        defaultCollectionShouldBeFound("price.specified=true")

        // Get all the collectionList where price is null
        defaultCollectionShouldNotBeFound("price.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price is greater than or equal to DEFAULT_PRICE
        defaultCollectionShouldBeFound("price.greaterThanOrEqual=$DEFAULT_PRICE")

        // Get all the collectionList where price is greater than or equal to UPDATED_PRICE
        defaultCollectionShouldNotBeFound("price.greaterThanOrEqual=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price is less than or equal to DEFAULT_PRICE
        defaultCollectionShouldBeFound("price.lessThanOrEqual=$DEFAULT_PRICE")

        // Get all the collectionList where price is less than or equal to SMALLER_PRICE
        defaultCollectionShouldNotBeFound("price.lessThanOrEqual=$SMALLER_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByPriceIsLessThanSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price is less than DEFAULT_PRICE
        defaultCollectionShouldNotBeFound("price.lessThan=$DEFAULT_PRICE")

        // Get all the collectionList where price is less than UPDATED_PRICE
        defaultCollectionShouldBeFound("price.lessThan=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCollectionsByPriceIsGreaterThanSomething() {
        // Initialize the database
        collectionRepository.saveAndFlush(collection)

        // Get all the collectionList where price is greater than DEFAULT_PRICE
        defaultCollectionShouldNotBeFound("price.greaterThan=$DEFAULT_PRICE")

        // Get all the collectionList where price is greater than SMALLER_PRICE
        defaultCollectionShouldBeFound("price.greaterThan=$SMALLER_PRICE")
    }

    @Test
    @Transactional
    fun getAllCollectionsByAvailablePackingsIsEqualToSomething() {
        // Initialize the database
        val availablePackings = PackingResourceIT.createEntity(em)
        em.persist(availablePackings)
        em.flush()
        collection.addAvailablePackings(availablePackings)
        collectionRepository.saveAndFlush(collection)
        val availablePackingsId = availablePackings.id

        // Get all the collectionList where availablePackings equals to availablePackingsId
        defaultCollectionShouldBeFound("availablePackingsId.equals=$availablePackingsId")

        // Get all the collectionList where availablePackings equals to availablePackingsId + 1
        defaultCollectionShouldNotBeFound("availablePackingsId.equals=${availablePackingsId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCollectionsByFlowersIsEqualToSomething() {
        // Initialize the database
        val flowers = FlowerResourceIT.createEntity(em)
        em.persist(flowers)
        em.flush()
        collection.addFlowers(flowers)
        collectionRepository.saveAndFlush(collection)
        val flowersId = flowers.id

        // Get all the collectionList where flowers equals to flowersId
        defaultCollectionShouldBeFound("flowersId.equals=$flowersId")

        // Get all the collectionList where flowers equals to flowersId + 1
        defaultCollectionShouldNotBeFound("flowersId.equals=${flowersId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCollectionsByCategoryIsEqualToSomething() {
        // Initialize the database
        val category = CategoryResourceIT.createEntity(em)
        em.persist(category)
        em.flush()
        collection.category = category
        collectionRepository.saveAndFlush(collection)
        val categoryId = category.id

        // Get all the collectionList where category equals to categoryId
        defaultCollectionShouldBeFound("categoryId.equals=$categoryId")

        // Get all the collectionList where category equals to categoryId + 1
        defaultCollectionShouldNotBeFound("categoryId.equals=${categoryId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultCollectionShouldBeFound(filter: String) {
        restCollectionMockMvc.perform(get("/api/collections?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))

        // Check, that the count call also returns 1
        restCollectionMockMvc.perform(get("/api/collections/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultCollectionShouldNotBeFound(filter: String) {
        restCollectionMockMvc.perform(get("/api/collections?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restCollectionMockMvc.perform(get("/api/collections/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingCollection() {
        // Get the collection
        restCollectionMockMvc.perform(get("/api/collections/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCollection() {
        // Initialize the database
        collectionService.save(collection)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCollectionSearchRepository)

        val databaseSizeBeforeUpdate = collectionRepository.findAll().size

        // Update the collection
        val id = collection.id
        assertNotNull(id)
        val updatedCollection = collectionRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCollection are not directly saved in db
        em.detach(updatedCollection)
        updatedCollection.name = UPDATED_NAME
        updatedCollection.description = UPDATED_DESCRIPTION
        updatedCollection.price = UPDATED_PRICE
        updatedCollection.image = UPDATED_IMAGE
        updatedCollection.imageContentType = UPDATED_IMAGE_CONTENT_TYPE

        restCollectionMockMvc.perform(
            put("/api/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCollection))
        ).andExpect(status().isOk)

        // Validate the Collection in the database
        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate)
        val testCollection = collectionList[collectionList.size - 1]
        assertThat(testCollection.name).isEqualTo(UPDATED_NAME)
        assertThat(testCollection.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testCollection.price).isEqualTo(UPDATED_PRICE)
        assertThat(testCollection.image).isEqualTo(UPDATED_IMAGE)
        assertThat(testCollection.imageContentType).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE)

        // Validate the Collection in Elasticsearch
        verify(mockCollectionSearchRepository, times(1)).save(testCollection)
    }

    @Test
    @Transactional
    fun updateNonExistingCollection() {
        val databaseSizeBeforeUpdate = collectionRepository.findAll().size

        // Create the Collection

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc.perform(
            put("/api/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collection))
        ).andExpect(status().isBadRequest)

        // Validate the Collection in the database
        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Collection in Elasticsearch
        verify(mockCollectionSearchRepository, times(0)).save(collection)
    }

    @Test
    @Transactional
    fun deleteCollection() {
        // Initialize the database
        collectionService.save(collection)

        val databaseSizeBeforeDelete = collectionRepository.findAll().size

        val id = collection.id
        assertNotNull(id)

        // Delete the collection
        restCollectionMockMvc.perform(
            delete("/api/collections/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val collectionList = collectionRepository.findAll()
        assertThat(collectionList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Collection in Elasticsearch
        verify(mockCollectionSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchCollection() {
        // InitializesearchCollection() the database
        collectionService.save(collection)
        `when`(mockCollectionSearchRepository.search(queryStringQuery("id:" + collection.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(listOf(collection), PageRequest.of(0, 1), 1))
        // Search the collection
        restCollectionMockMvc.perform(get("/api/_search/collections?query=id:" + collection.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val DEFAULT_PRICE: Double = 1.0
        private const val UPDATED_PRICE: Double = 2.0
        private const val SMALLER_PRICE: Double = 1.0 - 1.0

        private val DEFAULT_IMAGE: ByteArray = createByteArray(1, "0")
        private val UPDATED_IMAGE: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_IMAGE_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_IMAGE_CONTENT_TYPE: String = "image/png"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Collection {
            val collection = Collection(
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION,
                price = DEFAULT_PRICE,
                image = DEFAULT_IMAGE,
                imageContentType = DEFAULT_IMAGE_CONTENT_TYPE
            )

            return collection
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Collection {
            val collection = Collection(
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION,
                price = UPDATED_PRICE,
                image = UPDATED_IMAGE,
                imageContentType = UPDATED_IMAGE_CONTENT_TYPE
            )

            return collection
        }
    }
}
