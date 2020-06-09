package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.FlowerInCart
import com.team.flowershop.repository.FlowerInCartRepository
import com.team.flowershop.repository.search.FlowerInCartSearchRepository
import com.team.flowershop.service.FlowerInCartService
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
 * Integration tests for the [FlowerInCartResource] REST controller.
 *
 * @see FlowerInCartResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class FlowerInCartResourceIT {

    @Autowired
    private lateinit var flowerInCartRepository: FlowerInCartRepository

    @Autowired
    private lateinit var flowerInCartService: FlowerInCartService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.FlowerInCartSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockFlowerInCartSearchRepository: FlowerInCartSearchRepository

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

    private lateinit var restFlowerInCartMockMvc: MockMvc

    private lateinit var flowerInCart: FlowerInCart

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val flowerInCartResource = FlowerInCartResource(flowerInCartService)
        this.restFlowerInCartMockMvc = MockMvcBuilders.standaloneSetup(flowerInCartResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        flowerInCart = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createFlowerInCart() {
        val databaseSizeBeforeCreate = flowerInCartRepository.findAll().size

        // Create the FlowerInCart
        restFlowerInCartMockMvc.perform(
            post("/api/flower-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flowerInCart))
        ).andExpect(status().isCreated)

        // Validate the FlowerInCart in the database
        val flowerInCartList = flowerInCartRepository.findAll()
        assertThat(flowerInCartList).hasSize(databaseSizeBeforeCreate + 1)
        val testFlowerInCart = flowerInCartList[flowerInCartList.size - 1]
        assertThat(testFlowerInCart.amount).isEqualTo(DEFAULT_AMOUNT)

        // Validate the FlowerInCart in Elasticsearch
        verify(mockFlowerInCartSearchRepository, times(1)).save(testFlowerInCart)
    }

    @Test
    @Transactional
    fun createFlowerInCartWithExistingId() {
        val databaseSizeBeforeCreate = flowerInCartRepository.findAll().size

        // Create the FlowerInCart with an existing ID
        flowerInCart.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlowerInCartMockMvc.perform(
            post("/api/flower-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flowerInCart))
        ).andExpect(status().isBadRequest)

        // Validate the FlowerInCart in the database
        val flowerInCartList = flowerInCartRepository.findAll()
        assertThat(flowerInCartList).hasSize(databaseSizeBeforeCreate)

        // Validate the FlowerInCart in Elasticsearch
        verify(mockFlowerInCartSearchRepository, times(0)).save(flowerInCart)
    }

    @Test
    @Transactional
    fun getAllFlowerInCarts() {
        // Initialize the database
        flowerInCartRepository.saveAndFlush(flowerInCart)

        // Get all the flowerInCartList
        restFlowerInCartMockMvc.perform(get("/api/flower-in-carts?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flowerInCart.id?.toInt())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
    }

    @Test
    @Transactional
    fun getFlowerInCart() {
        // Initialize the database
        flowerInCartRepository.saveAndFlush(flowerInCart)

        val id = flowerInCart.id
        assertNotNull(id)

        // Get the flowerInCart
        restFlowerInCartMockMvc.perform(get("/api/flower-in-carts/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
    }

    @Test
    @Transactional
    fun getNonExistingFlowerInCart() {
        // Get the flowerInCart
        restFlowerInCartMockMvc.perform(get("/api/flower-in-carts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateFlowerInCart() {
        // Initialize the database
        flowerInCartService.save(flowerInCart)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockFlowerInCartSearchRepository)

        val databaseSizeBeforeUpdate = flowerInCartRepository.findAll().size

        // Update the flowerInCart
        val id = flowerInCart.id
        assertNotNull(id)
        val updatedFlowerInCart = flowerInCartRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedFlowerInCart are not directly saved in db
        em.detach(updatedFlowerInCart)
        updatedFlowerInCart.amount = UPDATED_AMOUNT

        restFlowerInCartMockMvc.perform(
            put("/api/flower-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedFlowerInCart))
        ).andExpect(status().isOk)

        // Validate the FlowerInCart in the database
        val flowerInCartList = flowerInCartRepository.findAll()
        assertThat(flowerInCartList).hasSize(databaseSizeBeforeUpdate)
        val testFlowerInCart = flowerInCartList[flowerInCartList.size - 1]
        assertThat(testFlowerInCart.amount).isEqualTo(UPDATED_AMOUNT)

        // Validate the FlowerInCart in Elasticsearch
        verify(mockFlowerInCartSearchRepository, times(1)).save(testFlowerInCart)
    }

    @Test
    @Transactional
    fun updateNonExistingFlowerInCart() {
        val databaseSizeBeforeUpdate = flowerInCartRepository.findAll().size

        // Create the FlowerInCart

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlowerInCartMockMvc.perform(
            put("/api/flower-in-carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flowerInCart))
        ).andExpect(status().isBadRequest)

        // Validate the FlowerInCart in the database
        val flowerInCartList = flowerInCartRepository.findAll()
        assertThat(flowerInCartList).hasSize(databaseSizeBeforeUpdate)

        // Validate the FlowerInCart in Elasticsearch
        verify(mockFlowerInCartSearchRepository, times(0)).save(flowerInCart)
    }

    @Test
    @Transactional
    fun deleteFlowerInCart() {
        // Initialize the database
        flowerInCartService.save(flowerInCart)

        val databaseSizeBeforeDelete = flowerInCartRepository.findAll().size

        val id = flowerInCart.id
        assertNotNull(id)

        // Delete the flowerInCart
        restFlowerInCartMockMvc.perform(
            delete("/api/flower-in-carts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val flowerInCartList = flowerInCartRepository.findAll()
        assertThat(flowerInCartList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the FlowerInCart in Elasticsearch
        verify(mockFlowerInCartSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchFlowerInCart() {
        // InitializesearchFlowerInCart() the database
        flowerInCartService.save(flowerInCart)
        `when`(mockFlowerInCartSearchRepository.search(queryStringQuery("id:" + flowerInCart.id)))
            .thenReturn(listOf(flowerInCart))
        // Search the flowerInCart
        restFlowerInCartMockMvc.perform(get("/api/_search/flower-in-carts?query=id:" + flowerInCart.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flowerInCart.id?.toInt())))
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
        fun createEntity(em: EntityManager): FlowerInCart {
            val flowerInCart = FlowerInCart(
                amount = DEFAULT_AMOUNT
            )

            return flowerInCart
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): FlowerInCart {
            val flowerInCart = FlowerInCart(
                amount = UPDATED_AMOUNT
            )

            return flowerInCart
        }
    }
}
