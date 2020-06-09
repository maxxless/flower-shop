package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Packing
import com.team.flowershop.repository.PackingRepository
import com.team.flowershop.repository.search.PackingSearchRepository
import com.team.flowershop.service.PackingQueryService
import com.team.flowershop.service.PackingService
import com.team.flowershop.web.rest.errors.ExceptionTranslator
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import org.springframework.validation.Validator

/**
 * Integration tests for the [PackingResource] REST controller.
 *
 * @see PackingResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class PackingResourceIT {

    @Autowired
    private lateinit var packingRepository: PackingRepository

    @Autowired
    private lateinit var packingService: PackingService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.PackingSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockPackingSearchRepository: PackingSearchRepository

    @Autowired
    private lateinit var packingQueryService: PackingQueryService

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

    private lateinit var restPackingMockMvc: MockMvc

    private lateinit var packing: Packing

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val packingResource = PackingResource(packingService, packingQueryService)
        this.restPackingMockMvc = MockMvcBuilders.standaloneSetup(packingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        packing = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPacking() {
        val databaseSizeBeforeCreate = packingRepository.findAll().size

        // Create the Packing
        restPackingMockMvc.perform(
            post("/api/packings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(packing))
        ).andExpect(status().isCreated)

        // Validate the Packing in the database
        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeCreate + 1)
        val testPacking = packingList[packingList.size - 1]
        assertThat(testPacking.name).isEqualTo(DEFAULT_NAME)
        assertThat(testPacking.material).isEqualTo(DEFAULT_MATERIAL)
        assertThat(testPacking.price).isEqualTo(DEFAULT_PRICE)

        // Validate the Packing in Elasticsearch
        verify(mockPackingSearchRepository, times(1)).save(testPacking)
    }

    @Test
    @Transactional
    fun createPackingWithExistingId() {
        val databaseSizeBeforeCreate = packingRepository.findAll().size

        // Create the Packing with an existing ID
        packing.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restPackingMockMvc.perform(
            post("/api/packings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(packing))
        ).andExpect(status().isBadRequest)

        // Validate the Packing in the database
        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeCreate)

        // Validate the Packing in Elasticsearch
        verify(mockPackingSearchRepository, times(0)).save(packing)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = packingRepository.findAll().size
        // set the field null
        packing.name = null

        // Create the Packing, which fails.

        restPackingMockMvc.perform(
            post("/api/packings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(packing))
        ).andExpect(status().isBadRequest)

        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceIsRequired() {
        val databaseSizeBeforeTest = packingRepository.findAll().size
        // set the field null
        packing.price = null

        // Create the Packing, which fails.

        restPackingMockMvc.perform(
            post("/api/packings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(packing))
        ).andExpect(status().isBadRequest)

        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllPackings() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList
        restPackingMockMvc.perform(get("/api/packings?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packing.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].material").value(hasItem(DEFAULT_MATERIAL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
    }

    @Test
    @Transactional
    fun getPacking() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        val id = packing.id
        assertNotNull(id)

        // Get the packing
        restPackingMockMvc.perform(get("/api/packings/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.material").value(DEFAULT_MATERIAL))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
    }

    @Test
    @Transactional
    fun getPackingsByIdFiltering() {
      // Initialize the database
      packingRepository.saveAndFlush(packing)
      val id = packing.id

      defaultPackingShouldBeFound("id.equals=" + id)
      defaultPackingShouldNotBeFound("id.notEquals=" + id)

      defaultPackingShouldBeFound("id.greaterThanOrEqual=" + id)
      defaultPackingShouldNotBeFound("id.greaterThan=" + id)

      defaultPackingShouldBeFound("id.lessThanOrEqual=" + id)
      defaultPackingShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    fun getAllPackingsByNameIsEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where name equals to DEFAULT_NAME
        defaultPackingShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the packingList where name equals to UPDATED_NAME
        defaultPackingShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByNameIsNotEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where name not equals to DEFAULT_NAME
        defaultPackingShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the packingList where name not equals to UPDATED_NAME
        defaultPackingShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    fun getAllPackingsByNameIsInShouldWork() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPackingShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the packingList where name equals to UPDATED_NAME
        defaultPackingShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByNameIsNullOrNotNull() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where name is not null
        defaultPackingShouldBeFound("name.specified=true")

        // Get all the packingList where name is null
        defaultPackingShouldNotBeFound("name.specified=false")
    }
                @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByNameContainsSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where name contains DEFAULT_NAME
        defaultPackingShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the packingList where name contains UPDATED_NAME
        defaultPackingShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByNameNotContainsSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where name does not contain DEFAULT_NAME
        defaultPackingShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the packingList where name does not contain UPDATED_NAME
        defaultPackingShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    fun getAllPackingsByMaterialIsEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where material equals to DEFAULT_MATERIAL
        defaultPackingShouldBeFound("material.equals=$DEFAULT_MATERIAL")

        // Get all the packingList where material equals to UPDATED_MATERIAL
        defaultPackingShouldNotBeFound("material.equals=$UPDATED_MATERIAL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByMaterialIsNotEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where material not equals to DEFAULT_MATERIAL
        defaultPackingShouldNotBeFound("material.notEquals=" + DEFAULT_MATERIAL)

        // Get all the packingList where material not equals to UPDATED_MATERIAL
        defaultPackingShouldBeFound("material.notEquals=" + UPDATED_MATERIAL)
    }

    @Test
    @Transactional
    fun getAllPackingsByMaterialIsInShouldWork() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where material in DEFAULT_MATERIAL or UPDATED_MATERIAL
        defaultPackingShouldBeFound("material.in=$DEFAULT_MATERIAL,$UPDATED_MATERIAL")

        // Get all the packingList where material equals to UPDATED_MATERIAL
        defaultPackingShouldNotBeFound("material.in=$UPDATED_MATERIAL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByMaterialIsNullOrNotNull() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where material is not null
        defaultPackingShouldBeFound("material.specified=true")

        // Get all the packingList where material is null
        defaultPackingShouldNotBeFound("material.specified=false")
    }
                @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByMaterialContainsSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where material contains DEFAULT_MATERIAL
        defaultPackingShouldBeFound("material.contains=" + DEFAULT_MATERIAL)

        // Get all the packingList where material contains UPDATED_MATERIAL
        defaultPackingShouldNotBeFound("material.contains=" + UPDATED_MATERIAL)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByMaterialNotContainsSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where material does not contain DEFAULT_MATERIAL
        defaultPackingShouldNotBeFound("material.doesNotContain=" + DEFAULT_MATERIAL)

        // Get all the packingList where material does not contain UPDATED_MATERIAL
        defaultPackingShouldBeFound("material.doesNotContain=" + UPDATED_MATERIAL)
    }

    @Test
    @Transactional
    fun getAllPackingsByPriceIsEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price equals to DEFAULT_PRICE
        defaultPackingShouldBeFound("price.equals=$DEFAULT_PRICE")

        // Get all the packingList where price equals to UPDATED_PRICE
        defaultPackingShouldNotBeFound("price.equals=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByPriceIsNotEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price not equals to DEFAULT_PRICE
        defaultPackingShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE)

        // Get all the packingList where price not equals to UPDATED_PRICE
        defaultPackingShouldBeFound("price.notEquals=" + UPDATED_PRICE)
    }

    @Test
    @Transactional
    fun getAllPackingsByPriceIsInShouldWork() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultPackingShouldBeFound("price.in=$DEFAULT_PRICE,$UPDATED_PRICE")

        // Get all the packingList where price equals to UPDATED_PRICE
        defaultPackingShouldNotBeFound("price.in=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByPriceIsNullOrNotNull() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price is not null
        defaultPackingShouldBeFound("price.specified=true")

        // Get all the packingList where price is null
        defaultPackingShouldNotBeFound("price.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price is greater than or equal to DEFAULT_PRICE
        defaultPackingShouldBeFound("price.greaterThanOrEqual=$DEFAULT_PRICE")

        // Get all the packingList where price is greater than or equal to UPDATED_PRICE
        defaultPackingShouldNotBeFound("price.greaterThanOrEqual=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price is less than or equal to DEFAULT_PRICE
        defaultPackingShouldBeFound("price.lessThanOrEqual=$DEFAULT_PRICE")

        // Get all the packingList where price is less than or equal to SMALLER_PRICE
        defaultPackingShouldNotBeFound("price.lessThanOrEqual=$SMALLER_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByPriceIsLessThanSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price is less than DEFAULT_PRICE
        defaultPackingShouldNotBeFound("price.lessThan=$DEFAULT_PRICE")

        // Get all the packingList where price is less than UPDATED_PRICE
        defaultPackingShouldBeFound("price.lessThan=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPackingsByPriceIsGreaterThanSomething() {
        // Initialize the database
        packingRepository.saveAndFlush(packing)

        // Get all the packingList where price is greater than DEFAULT_PRICE
        defaultPackingShouldNotBeFound("price.greaterThan=$DEFAULT_PRICE")

        // Get all the packingList where price is greater than SMALLER_PRICE
        defaultPackingShouldBeFound("price.greaterThan=$SMALLER_PRICE")
    }

    @Test
    @Transactional
    fun getAllPackingsByCollectionsIsEqualToSomething() {
        // Initialize the database
        val collections = CollectionResourceIT.createEntity(em)
        em.persist(collections)
        em.flush()
        packing.addCollections(collections)
        packingRepository.saveAndFlush(packing)
        val collectionsId = collections.id

        // Get all the packingList where collections equals to collectionsId
        defaultPackingShouldBeFound("collectionsId.equals=$collectionsId")

        // Get all the packingList where collections equals to collectionsId + 1
        defaultPackingShouldNotBeFound("collectionsId.equals=${collectionsId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultPackingShouldBeFound(filter: String) {
        restPackingMockMvc.perform(get("/api/packings?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packing.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].material").value(hasItem(DEFAULT_MATERIAL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))

        // Check, that the count call also returns 1
        restPackingMockMvc.perform(get("/api/packings/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultPackingShouldNotBeFound(filter: String) {
        restPackingMockMvc.perform(get("/api/packings?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restPackingMockMvc.perform(get("/api/packings/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingPacking() {
        // Get the packing
        restPackingMockMvc.perform(get("/api/packings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updatePacking() {
        // Initialize the database
        packingService.save(packing)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPackingSearchRepository)

        val databaseSizeBeforeUpdate = packingRepository.findAll().size

        // Update the packing
        val id = packing.id
        assertNotNull(id)
        val updatedPacking = packingRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedPacking are not directly saved in db
        em.detach(updatedPacking)
        updatedPacking.name = UPDATED_NAME
        updatedPacking.material = UPDATED_MATERIAL
        updatedPacking.price = UPDATED_PRICE

        restPackingMockMvc.perform(
            put("/api/packings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedPacking))
        ).andExpect(status().isOk)

        // Validate the Packing in the database
        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeUpdate)
        val testPacking = packingList[packingList.size - 1]
        assertThat(testPacking.name).isEqualTo(UPDATED_NAME)
        assertThat(testPacking.material).isEqualTo(UPDATED_MATERIAL)
        assertThat(testPacking.price).isEqualTo(UPDATED_PRICE)

        // Validate the Packing in Elasticsearch
        verify(mockPackingSearchRepository, times(1)).save(testPacking)
    }

    @Test
    @Transactional
    fun updateNonExistingPacking() {
        val databaseSizeBeforeUpdate = packingRepository.findAll().size

        // Create the Packing

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPackingMockMvc.perform(
            put("/api/packings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(packing))
        ).andExpect(status().isBadRequest)

        // Validate the Packing in the database
        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Packing in Elasticsearch
        verify(mockPackingSearchRepository, times(0)).save(packing)
    }

    @Test
    @Transactional
    fun deletePacking() {
        // Initialize the database
        packingService.save(packing)

        val databaseSizeBeforeDelete = packingRepository.findAll().size

        val id = packing.id
        assertNotNull(id)

        // Delete the packing
        restPackingMockMvc.perform(
            delete("/api/packings/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val packingList = packingRepository.findAll()
        assertThat(packingList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Packing in Elasticsearch
        verify(mockPackingSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchPacking() {
        // InitializesearchPacking() the database
        packingService.save(packing)
        `when`(mockPackingSearchRepository.search(queryStringQuery("id:" + packing.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(listOf(packing), PageRequest.of(0, 1), 1))
        // Search the packing
        restPackingMockMvc.perform(get("/api/_search/packings?query=id:" + packing.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packing.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].material").value(hasItem(DEFAULT_MATERIAL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_MATERIAL = "AAAAAAAAAA"
        private const val UPDATED_MATERIAL = "BBBBBBBBBB"

        private const val DEFAULT_PRICE: Double = 1.0
        private const val UPDATED_PRICE: Double = 2.0
        private const val SMALLER_PRICE: Double = 1.0 - 1.0

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Packing {
            val packing = Packing(
                name = DEFAULT_NAME,
                material = DEFAULT_MATERIAL,
                price = DEFAULT_PRICE
            )

            return packing
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Packing {
            val packing = Packing(
                name = UPDATED_NAME,
                material = UPDATED_MATERIAL,
                price = UPDATED_PRICE
            )

            return packing
        }
    }
}
