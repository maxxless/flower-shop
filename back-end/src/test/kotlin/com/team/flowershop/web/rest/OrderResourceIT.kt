package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Order
import com.team.flowershop.repository.OrderRepository
import com.team.flowershop.repository.search.OrderSearchRepository
import com.team.flowershop.service.OrderService
import com.team.flowershop.web.rest.errors.ExceptionTranslator
import java.time.Instant
import java.time.temporal.ChronoUnit
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
 * Integration tests for the [OrderResource] REST controller.
 *
 * @see OrderResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class OrderResourceIT {

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderService: OrderService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.OrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockOrderSearchRepository: OrderSearchRepository

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

    private lateinit var restOrderMockMvc: MockMvc

    private lateinit var order: Order

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val orderResource = OrderResource(orderService)
        this.restOrderMockMvc = MockMvcBuilders.standaloneSetup(orderResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        order = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createOrder() {
        val databaseSizeBeforeCreate = orderRepository.findAll().size

        // Create the Order
        restOrderMockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(order))
        ).andExpect(status().isCreated)

        // Validate the Order in the database
        val orderList = orderRepository.findAll()
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1)
        val testOrder = orderList[orderList.size - 1]
        assertThat(testOrder.totalPrice).isEqualTo(DEFAULT_TOTAL_PRICE)
        assertThat(testOrder.date).isEqualTo(DEFAULT_DATE)

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).save(testOrder)
    }

    @Test
    @Transactional
    fun createOrderWithExistingId() {
        val databaseSizeBeforeCreate = orderRepository.findAll().size

        // Create the Order with an existing ID
        order.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(order))
        ).andExpect(status().isBadRequest)

        // Validate the Order in the database
        val orderList = orderRepository.findAll()
        assertThat(orderList).hasSize(databaseSizeBeforeCreate)

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order)
    }

    @Test
    @Transactional
    fun getAllOrders() {
        // Initialize the database
        orderRepository.saveAndFlush(order)

        // Get all the orderList
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.id?.toInt())))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
    }

    @Test
    @Transactional
    fun getOrder() {
        // Initialize the database
        orderRepository.saveAndFlush(order)

        val id = order.id
        assertNotNull(id)

        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
    }

    @Test
    @Transactional
    fun getNonExistingOrder() {
        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateOrder() {
        // Initialize the database
        orderService.save(order)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockOrderSearchRepository)

        val databaseSizeBeforeUpdate = orderRepository.findAll().size

        // Update the order
        val id = order.id
        assertNotNull(id)
        val updatedOrder = orderRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder)
        updatedOrder.totalPrice = UPDATED_TOTAL_PRICE
        updatedOrder.date = UPDATED_DATE

        restOrderMockMvc.perform(
            put("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedOrder))
        ).andExpect(status().isOk)

        // Validate the Order in the database
        val orderList = orderRepository.findAll()
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate)
        val testOrder = orderList[orderList.size - 1]
        assertThat(testOrder.totalPrice).isEqualTo(UPDATED_TOTAL_PRICE)
        assertThat(testOrder.date).isEqualTo(UPDATED_DATE)

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).save(testOrder)
    }

    @Test
    @Transactional
    fun updateNonExistingOrder() {
        val databaseSizeBeforeUpdate = orderRepository.findAll().size

        // Create the Order

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc.perform(
            put("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(order))
        ).andExpect(status().isBadRequest)

        // Validate the Order in the database
        val orderList = orderRepository.findAll()
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order)
    }

    @Test
    @Transactional
    fun deleteOrder() {
        // Initialize the database
        orderService.save(order)

        val databaseSizeBeforeDelete = orderRepository.findAll().size

        val id = order.id
        assertNotNull(id)

        // Delete the order
        restOrderMockMvc.perform(
            delete("/api/orders/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val orderList = orderRepository.findAll()
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchOrder() {
        // InitializesearchOrder() the database
        orderService.save(order)
        `when`(mockOrderSearchRepository.search(queryStringQuery("id:" + order.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(listOf(order), PageRequest.of(0, 1), 1))
        // Search the order
        restOrderMockMvc.perform(get("/api/_search/orders?query=id:" + order.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.id?.toInt())))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
    }

    companion object {

        private const val DEFAULT_TOTAL_PRICE: Double = 1.0
        private const val UPDATED_TOTAL_PRICE: Double = 2.0

        private val DEFAULT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Order {
            val order = Order(
                totalPrice = DEFAULT_TOTAL_PRICE,
                date = DEFAULT_DATE
            )

            return order
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Order {
            val order = Order(
                totalPrice = UPDATED_TOTAL_PRICE,
                date = UPDATED_DATE
            )

            return order
        }
    }
}
