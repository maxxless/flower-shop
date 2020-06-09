package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.FlowerInOrder
import com.team.flowershop.repository.FlowerInOrderRepository
import com.team.flowershop.repository.search.FlowerInOrderSearchRepository
import com.team.flowershop.service.FlowerInOrderService
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
 * Integration tests for the [FlowerInOrderResource] REST controller.
 *
 * @see FlowerInOrderResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class FlowerInOrderResourceIT {

    @Autowired
    private lateinit var flowerInOrderRepository: FlowerInOrderRepository

    @Autowired
    private lateinit var flowerInOrderService: FlowerInOrderService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.FlowerInOrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockFlowerInOrderSearchRepository: FlowerInOrderSearchRepository

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

    private lateinit var restFlowerInOrderMockMvc: MockMvc

    private lateinit var flowerInOrder: FlowerInOrder

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val flowerInOrderResource = FlowerInOrderResource(flowerInOrderService)
        this.restFlowerInOrderMockMvc = MockMvcBuilders.standaloneSetup(flowerInOrderResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        flowerInOrder = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createFlowerInOrder() {
        val databaseSizeBeforeCreate = flowerInOrderRepository.findAll().size

        // Create the FlowerInOrder
        restFlowerInOrderMockMvc.perform(
            post("/api/flower-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flowerInOrder))
        ).andExpect(status().isCreated)

        // Validate the FlowerInOrder in the database
        val flowerInOrderList = flowerInOrderRepository.findAll()
        assertThat(flowerInOrderList).hasSize(databaseSizeBeforeCreate + 1)
        val testFlowerInOrder = flowerInOrderList[flowerInOrderList.size - 1]
        assertThat(testFlowerInOrder.amount).isEqualTo(DEFAULT_AMOUNT)

        // Validate the FlowerInOrder in Elasticsearch
        verify(mockFlowerInOrderSearchRepository, times(1)).save(testFlowerInOrder)
    }

    @Test
    @Transactional
    fun createFlowerInOrderWithExistingId() {
        val databaseSizeBeforeCreate = flowerInOrderRepository.findAll().size

        // Create the FlowerInOrder with an existing ID
        flowerInOrder.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlowerInOrderMockMvc.perform(
            post("/api/flower-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flowerInOrder))
        ).andExpect(status().isBadRequest)

        // Validate the FlowerInOrder in the database
        val flowerInOrderList = flowerInOrderRepository.findAll()
        assertThat(flowerInOrderList).hasSize(databaseSizeBeforeCreate)

        // Validate the FlowerInOrder in Elasticsearch
        verify(mockFlowerInOrderSearchRepository, times(0)).save(flowerInOrder)
    }

    @Test
    @Transactional
    fun getAllFlowerInOrders() {
        // Initialize the database
        flowerInOrderRepository.saveAndFlush(flowerInOrder)

        // Get all the flowerInOrderList
        restFlowerInOrderMockMvc.perform(get("/api/flower-in-orders?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flowerInOrder.id?.toInt())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
    }

    @Test
    @Transactional
    fun getFlowerInOrder() {
        // Initialize the database
        flowerInOrderRepository.saveAndFlush(flowerInOrder)

        val id = flowerInOrder.id
        assertNotNull(id)

        // Get the flowerInOrder
        restFlowerInOrderMockMvc.perform(get("/api/flower-in-orders/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
    }

    @Test
    @Transactional
    fun getNonExistingFlowerInOrder() {
        // Get the flowerInOrder
        restFlowerInOrderMockMvc.perform(get("/api/flower-in-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateFlowerInOrder() {
        // Initialize the database
        flowerInOrderService.save(flowerInOrder)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockFlowerInOrderSearchRepository)

        val databaseSizeBeforeUpdate = flowerInOrderRepository.findAll().size

        // Update the flowerInOrder
        val id = flowerInOrder.id
        assertNotNull(id)
        val updatedFlowerInOrder = flowerInOrderRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedFlowerInOrder are not directly saved in db
        em.detach(updatedFlowerInOrder)
        updatedFlowerInOrder.amount = UPDATED_AMOUNT

        restFlowerInOrderMockMvc.perform(
            put("/api/flower-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedFlowerInOrder))
        ).andExpect(status().isOk)

        // Validate the FlowerInOrder in the database
        val flowerInOrderList = flowerInOrderRepository.findAll()
        assertThat(flowerInOrderList).hasSize(databaseSizeBeforeUpdate)
        val testFlowerInOrder = flowerInOrderList[flowerInOrderList.size - 1]
        assertThat(testFlowerInOrder.amount).isEqualTo(UPDATED_AMOUNT)

        // Validate the FlowerInOrder in Elasticsearch
        verify(mockFlowerInOrderSearchRepository, times(1)).save(testFlowerInOrder)
    }

    @Test
    @Transactional
    fun updateNonExistingFlowerInOrder() {
        val databaseSizeBeforeUpdate = flowerInOrderRepository.findAll().size

        // Create the FlowerInOrder

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlowerInOrderMockMvc.perform(
            put("/api/flower-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(flowerInOrder))
        ).andExpect(status().isBadRequest)

        // Validate the FlowerInOrder in the database
        val flowerInOrderList = flowerInOrderRepository.findAll()
        assertThat(flowerInOrderList).hasSize(databaseSizeBeforeUpdate)

        // Validate the FlowerInOrder in Elasticsearch
        verify(mockFlowerInOrderSearchRepository, times(0)).save(flowerInOrder)
    }

    @Test
    @Transactional
    fun deleteFlowerInOrder() {
        // Initialize the database
        flowerInOrderService.save(flowerInOrder)

        val databaseSizeBeforeDelete = flowerInOrderRepository.findAll().size

        val id = flowerInOrder.id
        assertNotNull(id)

        // Delete the flowerInOrder
        restFlowerInOrderMockMvc.perform(
            delete("/api/flower-in-orders/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val flowerInOrderList = flowerInOrderRepository.findAll()
        assertThat(flowerInOrderList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the FlowerInOrder in Elasticsearch
        verify(mockFlowerInOrderSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchFlowerInOrder() {
        // InitializesearchFlowerInOrder() the database
        flowerInOrderService.save(flowerInOrder)
        `when`(mockFlowerInOrderSearchRepository.search(queryStringQuery("id:" + flowerInOrder.id)))
            .thenReturn(listOf(flowerInOrder))
        // Search the flowerInOrder
        restFlowerInOrderMockMvc.perform(get("/api/_search/flower-in-orders?query=id:" + flowerInOrder.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flowerInOrder.id?.toInt())))
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
        fun createEntity(em: EntityManager): FlowerInOrder {
            val flowerInOrder = FlowerInOrder(
                amount = DEFAULT_AMOUNT
            )

            return flowerInOrder
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): FlowerInOrder {
            val flowerInOrder = FlowerInOrder(
                amount = UPDATED_AMOUNT
            )

            return flowerInOrder
        }
    }
}
