package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.CollectionInCart
import com.team.flowershop.repository.CollectionInCartRepository
import com.team.flowershop.repository.search.CollectionInCartSearchRepository
import com.team.flowershop.service.CollectionInCartService
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
 * Integration tests for the [CollectionInCartResource] REST controller.
 *
 * @see CollectionInCartResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class CollectionInCartResourceIT {

    @Autowired
    private lateinit var collectionInCartRepository: CollectionInCartRepository

    @Autowired
    private lateinit var collectionInCartService: CollectionInCartService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.CollectionInCartSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockCollectionInCartSearchRepository: CollectionInCartSearchRepository

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

    private lateinit var restCollectionInCartMockMvc: MockMvc

    private lateinit var collectionInCart: CollectionInCart

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val collectionInCartResource = CollectionInCartResource(collectionInCartService)
        this.restCollectionInCartMockMvc = MockMvcBuilders.standaloneSetup(collectionInCartResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        collectionInCart = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCollectionInCart() {
        val databaseSizeBeforeCreate = collectionInCartRepository.findAll().size

        // Create the CollectionInCart
        restCollectionInCartMockMvc.perform(
            post("/api/collection-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collectionInCart))
        ).andExpect(status().isCreated)

        // Validate the CollectionInCart in the database
        val collectionInCartList = collectionInCartRepository.findAll()
        assertThat(collectionInCartList).hasSize(databaseSizeBeforeCreate + 1)
        val testCollectionInCart = collectionInCartList[collectionInCartList.size - 1]
        assertThat(testCollectionInCart.amount).isEqualTo(DEFAULT_AMOUNT)

        // Validate the CollectionInCart in Elasticsearch
        verify(mockCollectionInCartSearchRepository, times(1)).save(testCollectionInCart)
    }

    @Test
    @Transactional
    fun createCollectionInCartWithExistingId() {
        val databaseSizeBeforeCreate = collectionInCartRepository.findAll().size

        // Create the CollectionInCart with an existing ID
        collectionInCart.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionInCartMockMvc.perform(
            post("/api/collection-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collectionInCart))
        ).andExpect(status().isBadRequest)

        // Validate the CollectionInCart in the database
        val collectionInCartList = collectionInCartRepository.findAll()
        assertThat(collectionInCartList).hasSize(databaseSizeBeforeCreate)

        // Validate the CollectionInCart in Elasticsearch
        verify(mockCollectionInCartSearchRepository, times(0)).save(collectionInCart)
    }

    @Test
    @Transactional
    fun getAllCollectionInCarts() {
        // Initialize the database
        collectionInCartRepository.saveAndFlush(collectionInCart)

        // Get all the collectionInCartList
        restCollectionInCartMockMvc.perform(get("/api/collection-in-carts?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collectionInCart.id?.toInt())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
    }

    @Test
    @Transactional
    fun getCollectionInCart() {
        // Initialize the database
        collectionInCartRepository.saveAndFlush(collectionInCart)

        val id = collectionInCart.id
        assertNotNull(id)

        // Get the collectionInCart
        restCollectionInCartMockMvc.perform(get("/api/collection-in-carts/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
    }

    @Test
    @Transactional
    fun getNonExistingCollectionInCart() {
        // Get the collectionInCart
        restCollectionInCartMockMvc.perform(get("/api/collection-in-carts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCollectionInCart() {
        // Initialize the database
        collectionInCartService.save(collectionInCart)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCollectionInCartSearchRepository)

        val databaseSizeBeforeUpdate = collectionInCartRepository.findAll().size

        // Update the collectionInCart
        val id = collectionInCart.id
        assertNotNull(id)
        val updatedCollectionInCart = collectionInCartRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCollectionInCart are not directly saved in db
        em.detach(updatedCollectionInCart)
        updatedCollectionInCart.amount = UPDATED_AMOUNT

        restCollectionInCartMockMvc.perform(
            put("/api/collection-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCollectionInCart))
        ).andExpect(status().isOk)

        // Validate the CollectionInCart in the database
        val collectionInCartList = collectionInCartRepository.findAll()
        assertThat(collectionInCartList).hasSize(databaseSizeBeforeUpdate)
        val testCollectionInCart = collectionInCartList[collectionInCartList.size - 1]
        assertThat(testCollectionInCart.amount).isEqualTo(UPDATED_AMOUNT)

        // Validate the CollectionInCart in Elasticsearch
        verify(mockCollectionInCartSearchRepository, times(1)).save(testCollectionInCart)
    }

    @Test
    @Transactional
    fun updateNonExistingCollectionInCart() {
        val databaseSizeBeforeUpdate = collectionInCartRepository.findAll().size

        // Create the CollectionInCart

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionInCartMockMvc.perform(
            put("/api/collection-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collectionInCart))
        ).andExpect(status().isBadRequest)

        // Validate the CollectionInCart in the database
        val collectionInCartList = collectionInCartRepository.findAll()
        assertThat(collectionInCartList).hasSize(databaseSizeBeforeUpdate)

        // Validate the CollectionInCart in Elasticsearch
        verify(mockCollectionInCartSearchRepository, times(0)).save(collectionInCart)
    }

    @Test
    @Transactional
    fun deleteCollectionInCart() {
        // Initialize the database
        collectionInCartService.save(collectionInCart)

        val databaseSizeBeforeDelete = collectionInCartRepository.findAll().size

        val id = collectionInCart.id
        assertNotNull(id)

        // Delete the collectionInCart
        restCollectionInCartMockMvc.perform(
            delete("/api/collection-in-carts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val collectionInCartList = collectionInCartRepository.findAll()
        assertThat(collectionInCartList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the CollectionInCart in Elasticsearch
        verify(mockCollectionInCartSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchCollectionInCart() {
        // InitializesearchCollectionInCart() the database
        collectionInCartService.save(collectionInCart)
        `when`(mockCollectionInCartSearchRepository.search(queryStringQuery("id:" + collectionInCart.id)))
            .thenReturn(listOf(collectionInCart))
        // Search the collectionInCart
        restCollectionInCartMockMvc.perform(get("/api/_search/collection-in-carts?query=id:" + collectionInCart.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collectionInCart.id?.toInt())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
    }

    companion object {

        private const val DEFAULT_AMOUNT: Int = 1
        private const val UPDATED_AMOUNT: Int = 2

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): CollectionInCart {
            val collectionInCart = CollectionInCart(
                amount = DEFAULT_AMOUNT
            )

            return collectionInCart
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): CollectionInCart {
            val collectionInCart = CollectionInCart(
                amount = UPDATED_AMOUNT
            )

            return collectionInCart
        }
    }
}
