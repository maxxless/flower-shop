package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Flower
import com.team.flowershop.repository.FlowerRepository
import com.team.flowershop.repository.search.FlowerSearchRepository
import com.team.flowershop.service.FlowerQueryService
import com.team.flowershop.service.FlowerService
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
 * Integration tests for the [FlowerResource] REST controller.
 *
 * @see FlowerResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class FlowerResourceIT {

    @Autowired
    private lateinit var flowerRepository: FlowerRepository

    @Mock
    private lateinit var flowerRepositoryMock: FlowerRepository

    @Mock
    private lateinit var flowerServiceMock: FlowerService

    @Autowired
    private lateinit var flowerService: FlowerService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.FlowerSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockFlowerSearchRepository: FlowerSearchRepository

    @Autowired
    private lateinit var flowerQueryService: FlowerQueryService

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

    private lateinit var restFlowerMockMvc: MockMvc

    private lateinit var flower: Flower

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val flowerResource = FlowerResource(flowerService, flowerQueryService)
        this.restFlowerMockMvc = MockMvcBuilders.standaloneSetup(flowerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        flower = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createFlower() {
        val databaseSizeBeforeCreate = flowerRepository.findAll().size

        // Create the Flower
        restFlowerMockMvc.perform(
            post("/api/flowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flower))
        ).andExpect(status().isCreated)

        // Validate the Flower in the database
        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeCreate + 1)
        val testFlower = flowerList[flowerList.size - 1]
        assertThat(testFlower.name).isEqualTo(DEFAULT_NAME)
        assertThat(testFlower.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testFlower.price).isEqualTo(DEFAULT_PRICE)
        assertThat(testFlower.image).isEqualTo(DEFAULT_IMAGE)
        assertThat(testFlower.imageContentType).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE)

        // Validate the Flower in Elasticsearch
        verify(mockFlowerSearchRepository, times(1)).save(testFlower)
    }

    @Test
    @Transactional
    fun createFlowerWithExistingId() {
        val databaseSizeBeforeCreate = flowerRepository.findAll().size

        // Create the Flower with an existing ID
        flower.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlowerMockMvc.perform(
            post("/api/flowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flower))
        ).andExpect(status().isBadRequest)

        // Validate the Flower in the database
        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeCreate)

        // Validate the Flower in Elasticsearch
        verify(mockFlowerSearchRepository, times(0)).save(flower)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = flowerRepository.findAll().size
        // set the field null
        flower.name = null

        // Create the Flower, which fails.

        restFlowerMockMvc.perform(
            post("/api/flowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flower))
        ).andExpect(status().isBadRequest)

        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceIsRequired() {
        val databaseSizeBeforeTest = flowerRepository.findAll().size
        // set the field null
        flower.price = null

        // Create the Flower, which fails.

        restFlowerMockMvc.perform(
            post("/api/flowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flower))
        ).andExpect(status().isBadRequest)

        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllFlowers() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList
        restFlowerMockMvc.perform(get("/api/flowers?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flower.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
    }

    @Suppress("unchecked")
    fun getAllFlowersWithEagerRelationshipsIsEnabled() {
        val flowerResource = FlowerResource(flowerServiceMock, flowerQueryService)
        `when`(flowerServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restFlowerMockMvc = MockMvcBuilders.standaloneSetup(flowerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restFlowerMockMvc.perform(get("/api/flowers?eagerload=true"))
            .andExpect(status().isOk)

        verify(flowerServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    fun getAllFlowersWithEagerRelationshipsIsNotEnabled() {
        val flowerResource = FlowerResource(flowerServiceMock, flowerQueryService)
            `when`(flowerServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restFlowerMockMvc = MockMvcBuilders.standaloneSetup(flowerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restFlowerMockMvc.perform(get("/api/flowers?eagerload=true"))
            .andExpect(status().isOk)

        verify(flowerServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getFlower() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        val id = flower.id
        assertNotNull(id)

        // Get the flower
        restFlowerMockMvc.perform(get("/api/flowers/{id}", id))
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
    fun getFlowersByIdFiltering() {
      // Initialize the database
      flowerRepository.saveAndFlush(flower)
      val id = flower.id

      defaultFlowerShouldBeFound("id.equals=" + id)
      defaultFlowerShouldNotBeFound("id.notEquals=" + id)

      defaultFlowerShouldBeFound("id.greaterThanOrEqual=" + id)
      defaultFlowerShouldNotBeFound("id.greaterThan=" + id)

      defaultFlowerShouldBeFound("id.lessThanOrEqual=" + id)
      defaultFlowerShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    fun getAllFlowersByNameIsEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where name equals to DEFAULT_NAME
        defaultFlowerShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the flowerList where name equals to UPDATED_NAME
        defaultFlowerShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByNameIsNotEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where name not equals to DEFAULT_NAME
        defaultFlowerShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the flowerList where name not equals to UPDATED_NAME
        defaultFlowerShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    fun getAllFlowersByNameIsInShouldWork() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFlowerShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the flowerList where name equals to UPDATED_NAME
        defaultFlowerShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByNameIsNullOrNotNull() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where name is not null
        defaultFlowerShouldBeFound("name.specified=true")

        // Get all the flowerList where name is null
        defaultFlowerShouldNotBeFound("name.specified=false")
    }
                @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByNameContainsSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where name contains DEFAULT_NAME
        defaultFlowerShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the flowerList where name contains UPDATED_NAME
        defaultFlowerShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByNameNotContainsSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where name does not contain DEFAULT_NAME
        defaultFlowerShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the flowerList where name does not contain UPDATED_NAME
        defaultFlowerShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    fun getAllFlowersByDescriptionIsEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where description equals to DEFAULT_DESCRIPTION
        defaultFlowerShouldBeFound("description.equals=$DEFAULT_DESCRIPTION")

        // Get all the flowerList where description equals to UPDATED_DESCRIPTION
        defaultFlowerShouldNotBeFound("description.equals=$UPDATED_DESCRIPTION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByDescriptionIsNotEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where description not equals to DEFAULT_DESCRIPTION
        defaultFlowerShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION)

        // Get all the flowerList where description not equals to UPDATED_DESCRIPTION
        defaultFlowerShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun getAllFlowersByDescriptionIsInShouldWork() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultFlowerShouldBeFound("description.in=$DEFAULT_DESCRIPTION,$UPDATED_DESCRIPTION")

        // Get all the flowerList where description equals to UPDATED_DESCRIPTION
        defaultFlowerShouldNotBeFound("description.in=$UPDATED_DESCRIPTION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByDescriptionIsNullOrNotNull() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where description is not null
        defaultFlowerShouldBeFound("description.specified=true")

        // Get all the flowerList where description is null
        defaultFlowerShouldNotBeFound("description.specified=false")
    }
                @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByDescriptionContainsSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where description contains DEFAULT_DESCRIPTION
        defaultFlowerShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION)

        // Get all the flowerList where description contains UPDATED_DESCRIPTION
        defaultFlowerShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByDescriptionNotContainsSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where description does not contain DEFAULT_DESCRIPTION
        defaultFlowerShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION)

        // Get all the flowerList where description does not contain UPDATED_DESCRIPTION
        defaultFlowerShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun getAllFlowersByPriceIsEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price equals to DEFAULT_PRICE
        defaultFlowerShouldBeFound("price.equals=$DEFAULT_PRICE")

        // Get all the flowerList where price equals to UPDATED_PRICE
        defaultFlowerShouldNotBeFound("price.equals=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByPriceIsNotEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price not equals to DEFAULT_PRICE
        defaultFlowerShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE)

        // Get all the flowerList where price not equals to UPDATED_PRICE
        defaultFlowerShouldBeFound("price.notEquals=" + UPDATED_PRICE)
    }

    @Test
    @Transactional
    fun getAllFlowersByPriceIsInShouldWork() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultFlowerShouldBeFound("price.in=$DEFAULT_PRICE,$UPDATED_PRICE")

        // Get all the flowerList where price equals to UPDATED_PRICE
        defaultFlowerShouldNotBeFound("price.in=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByPriceIsNullOrNotNull() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price is not null
        defaultFlowerShouldBeFound("price.specified=true")

        // Get all the flowerList where price is null
        defaultFlowerShouldNotBeFound("price.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price is greater than or equal to DEFAULT_PRICE
        defaultFlowerShouldBeFound("price.greaterThanOrEqual=$DEFAULT_PRICE")

        // Get all the flowerList where price is greater than or equal to UPDATED_PRICE
        defaultFlowerShouldNotBeFound("price.greaterThanOrEqual=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price is less than or equal to DEFAULT_PRICE
        defaultFlowerShouldBeFound("price.lessThanOrEqual=$DEFAULT_PRICE")

        // Get all the flowerList where price is less than or equal to SMALLER_PRICE
        defaultFlowerShouldNotBeFound("price.lessThanOrEqual=$SMALLER_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByPriceIsLessThanSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price is less than DEFAULT_PRICE
        defaultFlowerShouldNotBeFound("price.lessThan=$DEFAULT_PRICE")

        // Get all the flowerList where price is less than UPDATED_PRICE
        defaultFlowerShouldBeFound("price.lessThan=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFlowersByPriceIsGreaterThanSomething() {
        // Initialize the database
        flowerRepository.saveAndFlush(flower)

        // Get all the flowerList where price is greater than DEFAULT_PRICE
        defaultFlowerShouldNotBeFound("price.greaterThan=$DEFAULT_PRICE")

        // Get all the flowerList where price is greater than SMALLER_PRICE
        defaultFlowerShouldBeFound("price.greaterThan=$SMALLER_PRICE")
    }

    @Test
    @Transactional
    fun getAllFlowersByAvailableColoursIsEqualToSomething() {
        // Initialize the database
        val availableColours = ColourResourceIT.createEntity(em)
        em.persist(availableColours)
        em.flush()
        flower.addAvailableColours(availableColours)
        flowerRepository.saveAndFlush(flower)
        val availableColoursId = availableColours.id

        // Get all the flowerList where availableColours equals to availableColoursId
        defaultFlowerShouldBeFound("availableColoursId.equals=$availableColoursId")

        // Get all the flowerList where availableColours equals to availableColoursId + 1
        defaultFlowerShouldNotBeFound("availableColoursId.equals=${availableColoursId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllFlowersByCollectionsInIsEqualToSomething() {
        // Initialize the database
        val collectionsIn = CollectionResourceIT.createEntity(em)
        em.persist(collectionsIn)
        em.flush()
        flower.addCollectionsIn(collectionsIn)
        flowerRepository.saveAndFlush(flower)
        val collectionsInId = collectionsIn.id

        // Get all the flowerList where collectionsIn equals to collectionsInId
        defaultFlowerShouldBeFound("collectionsInId.equals=$collectionsInId")

        // Get all the flowerList where collectionsIn equals to collectionsInId + 1
        defaultFlowerShouldNotBeFound("collectionsInId.equals=${collectionsInId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultFlowerShouldBeFound(filter: String) {
        restFlowerMockMvc.perform(get("/api/flowers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flower.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))

        // Check, that the count call also returns 1
        restFlowerMockMvc.perform(get("/api/flowers/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultFlowerShouldNotBeFound(filter: String) {
        restFlowerMockMvc.perform(get("/api/flowers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restFlowerMockMvc.perform(get("/api/flowers/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingFlower() {
        // Get the flower
        restFlowerMockMvc.perform(get("/api/flowers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateFlower() {
        // Initialize the database
        flowerService.save(flower)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockFlowerSearchRepository)

        val databaseSizeBeforeUpdate = flowerRepository.findAll().size

        // Update the flower
        val id = flower.id
        assertNotNull(id)
        val updatedFlower = flowerRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedFlower are not directly saved in db
        em.detach(updatedFlower)
        updatedFlower.name = UPDATED_NAME
        updatedFlower.description = UPDATED_DESCRIPTION
        updatedFlower.price = UPDATED_PRICE
        updatedFlower.image = UPDATED_IMAGE
        updatedFlower.imageContentType = UPDATED_IMAGE_CONTENT_TYPE

        restFlowerMockMvc.perform(
            put("/api/flowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedFlower))
        ).andExpect(status().isOk)

        // Validate the Flower in the database
        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeUpdate)
        val testFlower = flowerList[flowerList.size - 1]
        assertThat(testFlower.name).isEqualTo(UPDATED_NAME)
        assertThat(testFlower.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testFlower.price).isEqualTo(UPDATED_PRICE)
        assertThat(testFlower.image).isEqualTo(UPDATED_IMAGE)
        assertThat(testFlower.imageContentType).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE)

        // Validate the Flower in Elasticsearch
        verify(mockFlowerSearchRepository, times(1)).save(testFlower)
    }

    @Test
    @Transactional
    fun updateNonExistingFlower() {
        val databaseSizeBeforeUpdate = flowerRepository.findAll().size

        // Create the Flower

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlowerMockMvc.perform(
            put("/api/flowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flower))
        ).andExpect(status().isBadRequest)

        // Validate the Flower in the database
        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Flower in Elasticsearch
        verify(mockFlowerSearchRepository, times(0)).save(flower)
    }

    @Test
    @Transactional
    fun deleteFlower() {
        // Initialize the database
        flowerService.save(flower)

        val databaseSizeBeforeDelete = flowerRepository.findAll().size

        val id = flower.id
        assertNotNull(id)

        // Delete the flower
        restFlowerMockMvc.perform(
            delete("/api/flowers/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val flowerList = flowerRepository.findAll()
        assertThat(flowerList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Flower in Elasticsearch
        verify(mockFlowerSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchFlower() {
        // InitializesearchFlower() the database
        flowerService.save(flower)
        `when`(mockFlowerSearchRepository.search(queryStringQuery("id:" + flower.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(listOf(flower), PageRequest.of(0, 1), 1))
        // Search the flower
        restFlowerMockMvc.perform(get("/api/_search/flowers?query=id:" + flower.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flower.id?.toInt())))
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
        fun createEntity(em: EntityManager): Flower {
            val flower = Flower(
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION,
                price = DEFAULT_PRICE,
                image = DEFAULT_IMAGE,
                imageContentType = DEFAULT_IMAGE_CONTENT_TYPE
            )

            return flower
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Flower {
            val flower = Flower(
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION,
                price = UPDATED_PRICE,
                image = UPDATED_IMAGE,
                imageContentType = UPDATED_IMAGE_CONTENT_TYPE
            )

            return flower
        }
    }
}
