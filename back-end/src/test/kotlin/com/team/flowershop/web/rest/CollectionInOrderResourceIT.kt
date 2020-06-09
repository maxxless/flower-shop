package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.CollectionInOrder
import com.team.flowershop.repository.CollectionInOrderRepository
import com.team.flowershop.repository.search.CollectionInOrderSearchRepository
import com.team.flowershop.service.CollectionInOrderService
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
 * Integration tests for the [CollectionInOrderResource] REST controller.
 *
 * @see CollectionInOrderResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class CollectionInOrderResourceIT {

    @Autowired
    private lateinit var collectionInOrderRepository: CollectionInOrderRepository

    @Autowired
    private lateinit var collectionInOrderService: CollectionInOrderService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.CollectionInOrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockCollectionInOrderSearchRepository: CollectionInOrderSearchRepository

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

    private lateinit var restCollectionInOrderMockMvc: MockMvc

    private lateinit var collectionInOrder: CollectionInOrder

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val collectionInOrderResource = CollectionInOrderResource(collectionInOrderService)
        this.restCollectionInOrderMockMvc = MockMvcBuilders.standaloneSetup(collectionInOrderResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        collectionInOrder = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCollectionInOrder() {
        val databaseSizeBeforeCreate = collectionInOrderRepository.findAll().size

        // Create the CollectionInOrder
        restCollectionInOrderMockMvc.perform(
            post("/api/collection-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collectionInOrder))
        ).andExpect(status().isCreated)

        // Validate the CollectionInOrder in the database
        val collectionInOrderList = collectionInOrderRepository.findAll()
        assertThat(collectionInOrderList).hasSize(databaseSizeBeforeCreate + 1)
        val testCollectionInOrder = collectionInOrderList[collectionInOrderList.size - 1]
        assertThat(testCollectionInOrder.amount).isEqualTo(DEFAULT_AMOUNT)

        // Validate the CollectionInOrder in Elasticsearch
        verify(mockCollectionInOrderSearchRepository, times(1)).save(testCollectionInOrder)
    }

    @Test
    @Transactional
    fun createCollectionInOrderWithExistingId() {
        val databaseSizeBeforeCreate = collectionInOrderRepository.findAll().size

        // Create the CollectionInOrder with an existing ID
        collectionInOrder.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionInOrderMockMvc.perform(
            post("/api/collection-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collectionInOrder))
        ).andExpect(status().isBadRequest)

        // Validate the CollectionInOrder in the database
        val collectionInOrderList = collectionInOrderRepository.findAll()
        assertThat(collectionInOrderList).hasSize(databaseSizeBeforeCreate)

        // Validate the CollectionInOrder in Elasticsearch
        verify(mockCollectionInOrderSearchRepository, times(0)).save(collectionInOrder)
    }

    @Test
    @Transactional
    fun getAllCollectionInOrders() {
        // Initialize the database
        collectionInOrderRepository.saveAndFlush(collectionInOrder)

        // Get all the collectionInOrderList
        restCollectionInOrderMockMvc.perform(get("/api/collection-in-orders?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collectionInOrder.id?.toInt())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
    }

    @Test
    @Transactional
    fun getCollectionInOrder() {
        // Initialize the database
        collectionInOrderRepository.saveAndFlush(collectionInOrder)

        val id = collectionInOrder.id
        assertNotNull(id)

        // Get the collectionInOrder
        restCollectionInOrderMockMvc.perform(get("/api/collection-in-orders/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
    }

    @Test
    @Transactional
    fun getNonExistingCollectionInOrder() {
        // Get the collectionInOrder
        restCollectionInOrderMockMvc.perform(get("/api/collection-in-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCollectionInOrder() {
        // Initialize the database
        collectionInOrderService.save(collectionInOrder)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCollectionInOrderSearchRepository)

        val databaseSizeBeforeUpdate = collectionInOrderRepository.findAll().size

        // Update the collectionInOrder
        val id = collectionInOrder.id
        assertNotNull(id)
        val updatedCollectionInOrder = collectionInOrderRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCollectionInOrder are not directly saved in db
        em.detach(updatedCollectionInOrder)
        updatedCollectionInOrder.amount = UPDATED_AMOUNT

        restCollectionInOrderMockMvc.perform(
            put("/api/collection-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCollectionInOrder))
        ).andExpect(status().isOk)

        // Validate the CollectionInOrder in the database
        val collectionInOrderList = collectionInOrderRepository.findAll()
        assertThat(collectionInOrderList).hasSize(databaseSizeBeforeUpdate)
        val testCollectionInOrder = collectionInOrderList[collectionInOrderList.size - 1]
        assertThat(testCollectionInOrder.amount).isEqualTo(UPDATED_AMOUNT)

        // Validate the CollectionInOrder in Elasticsearch
        verify(mockCollectionInOrderSearchRepository, times(1)).save(testCollectionInOrder)
    }

    @Test
    @Transactional
    fun updateNonExistingCollectionInOrder() {
        val databaseSizeBeforeUpdate = collectionInOrderRepository.findAll().size

        // Create the CollectionInOrder

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionInOrderMockMvc.perform(
            put("/api/collection-in-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(collectionInOrder))
        ).andExpect(status().isBadRequest)

        // Validate the CollectionInOrder in the database
        val collectionInOrderList = collectionInOrderRepository.findAll()
        assertThat(collectionInOrderList).hasSize(databaseSizeBeforeUpdate)

        // Validate the CollectionInOrder in Elasticsearch
        verify(mockCollectionInOrderSearchRepository, times(0)).save(collectionInOrder)
    }

    @Test
    @Transactional
    fun deleteCollectionInOrder() {
        // Initialize the database
        collectionInOrderService.save(collectionInOrder)

        val databaseSizeBeforeDelete = collectionInOrderRepository.findAll().size

        val id = collectionInOrder.id
        assertNotNull(id)

        // Delete the collectionInOrder
        restCollectionInOrderMockMvc.perform(
            delete("/api/collection-in-orders/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val collectionInOrderList = collectionInOrderRepository.findAll()
        assertThat(collectionInOrderList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the CollectionInOrder in Elasticsearch
        verify(mockCollectionInOrderSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchCollectionInOrder() {
        // InitializesearchCollectionInOrder() the database
        collectionInOrderService.save(collectionInOrder)
        `when`(mockCollectionInOrderSearchRepository.search(queryStringQuery("id:" + collectionInOrder.id)))
            .thenReturn(listOf(collectionInOrder))
        // Search the collectionInOrder
        restCollectionInOrderMockMvc.perform(get("/api/_search/collection-in-orders?query=id:" + collectionInOrder.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collectionInOrder.id?.toInt())))
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
        fun createEntity(em: EntityManager): CollectionInOrder {
            val collectionInOrder = CollectionInOrder(
                amount = DEFAULT_AMOUNT
            )

            return collectionInOrder
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): CollectionInOrder {
            val collectionInOrder = CollectionInOrder(
                amount = UPDATED_AMOUNT
            )

            return collectionInOrder
        }
    }
}
