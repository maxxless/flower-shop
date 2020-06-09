package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Delivery
import com.team.flowershop.domain.Order
import com.team.flowershop.domain.enumeration.DeliveryType
import com.team.flowershop.repository.DeliveryRepository
import com.team.flowershop.repository.search.DeliverySearchRepository
import com.team.flowershop.service.DeliveryService
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
 * Integration tests for the [DeliveryResource] REST controller.
 *
 * @see DeliveryResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class DeliveryResourceIT {

    @Autowired
    private lateinit var deliveryRepository: DeliveryRepository

    @Autowired
    private lateinit var deliveryService: DeliveryService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.DeliverySearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockDeliverySearchRepository: DeliverySearchRepository

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

    private lateinit var restDeliveryMockMvc: MockMvc

    private lateinit var delivery: Delivery

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val deliveryResource = DeliveryResource(deliveryService)
        this.restDeliveryMockMvc = MockMvcBuilders.standaloneSetup(deliveryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        delivery = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createDelivery() {
        val databaseSizeBeforeCreate = deliveryRepository.findAll().size

        // Create the Delivery
        restDeliveryMockMvc.perform(
            post("/api/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(delivery))
        ).andExpect(status().isCreated)

        // Validate the Delivery in the database
        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeCreate + 1)
        val testDelivery = deliveryList[deliveryList.size - 1]
        assertThat(testDelivery.address).isEqualTo(DEFAULT_ADDRESS)
        assertThat(testDelivery.postOfficeNumber).isEqualTo(DEFAULT_POST_OFFICE_NUMBER)
        assertThat(testDelivery.price).isEqualTo(DEFAULT_PRICE)
        assertThat(testDelivery.type).isEqualTo(DEFAULT_TYPE)

        // Validate the id for MapsId, the ids must be same
        assertThat(testDelivery.id).isEqualTo(testDelivery.order?.id)

        // Validate the Delivery in Elasticsearch
        verify(mockDeliverySearchRepository, times(1)).save(testDelivery)
    }

    @Test
    @Transactional
    fun createDeliveryWithExistingId() {
        val databaseSizeBeforeCreate = deliveryRepository.findAll().size

        // Create the Delivery with an existing ID
        delivery.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryMockMvc.perform(
            post("/api/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(delivery))
        ).andExpect(status().isBadRequest)

        // Validate the Delivery in the database
        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeCreate)

        // Validate the Delivery in Elasticsearch
        verify(mockDeliverySearchRepository, times(0)).save(delivery)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun updateDeliveryMapsIdAssociationWithNewId() {
        // Initialize the database
        deliveryService.save(delivery)
        val databaseSizeBeforeCreate = deliveryRepository.findAll().size

        // Add a new parent entity
        val order = OrderResourceIT.createEntity(em)
        em.persist(order)
        em.flush()

        // Load the delivery
        val updatedDelivery = deliveryRepository.findById(delivery.id).get()
        // Disconnect from session so that the updates on updatedDelivery are not directly saved in db
        em.detach(updatedDelivery)

        // Update the Order with new association value
        updatedDelivery.order = order

        // Update the entity
        restDeliveryMockMvc.perform(put("/api/deliveries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(updatedDelivery)))
            .andExpect(status().isOk)

        // Validate the Delivery in the database
        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeCreate)
        val testDelivery = deliveryList.get(deliveryList.size - 1)

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testDelivery.id).isEqualTo(testDelivery.order?.id)

        // Validate the Delivery in Elasticsearch
        verify(mockDeliverySearchRepository, times(0)).save(delivery)
    }

    @Test
    @Transactional
    fun checkAddressIsRequired() {
        val databaseSizeBeforeTest = deliveryRepository.findAll().size
        // set the field null
        delivery.address = null

        // Create the Delivery, which fails.

        restDeliveryMockMvc.perform(
            post("/api/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(delivery))
        ).andExpect(status().isBadRequest)

        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllDeliveries() {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery)

        // Get all the deliveryList
        restDeliveryMockMvc.perform(get("/api/deliveries?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.id?.toInt())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].postOfficeNumber").value(hasItem(DEFAULT_POST_OFFICE_NUMBER)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
    }

    @Test
    @Transactional
    fun getDelivery() {
        // Initialize the database
        deliveryRepository.saveAndFlush(delivery)

        val id = delivery.id
        assertNotNull(id)

        // Get the delivery
        restDeliveryMockMvc.perform(get("/api/deliveries/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.postOfficeNumber").value(DEFAULT_POST_OFFICE_NUMBER))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
    }

    @Test
    @Transactional
    fun getNonExistingDelivery() {
        // Get the delivery
        restDeliveryMockMvc.perform(get("/api/deliveries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateDelivery() {
        // Initialize the database
        deliveryService.save(delivery)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockDeliverySearchRepository)

        val databaseSizeBeforeUpdate = deliveryRepository.findAll().size

        // Update the delivery
        val id = delivery.id
        assertNotNull(id)
        val updatedDelivery = deliveryRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedDelivery are not directly saved in db
        em.detach(updatedDelivery)
        updatedDelivery.address = UPDATED_ADDRESS
        updatedDelivery.postOfficeNumber = UPDATED_POST_OFFICE_NUMBER
        updatedDelivery.price = UPDATED_PRICE
        updatedDelivery.type = UPDATED_TYPE

        restDeliveryMockMvc.perform(
            put("/api/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedDelivery))
        ).andExpect(status().isOk)

        // Validate the Delivery in the database
        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate)
        val testDelivery = deliveryList[deliveryList.size - 1]
        assertThat(testDelivery.address).isEqualTo(UPDATED_ADDRESS)
        assertThat(testDelivery.postOfficeNumber).isEqualTo(UPDATED_POST_OFFICE_NUMBER)
        assertThat(testDelivery.price).isEqualTo(UPDATED_PRICE)
        assertThat(testDelivery.type).isEqualTo(UPDATED_TYPE)

        // Validate the Delivery in Elasticsearch
        verify(mockDeliverySearchRepository, times(1)).save(testDelivery)
    }

    @Test
    @Transactional
    fun updateNonExistingDelivery() {
        val databaseSizeBeforeUpdate = deliveryRepository.findAll().size

        // Create the Delivery

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc.perform(
            put("/api/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(delivery))
        ).andExpect(status().isBadRequest)

        // Validate the Delivery in the database
        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Delivery in Elasticsearch
        verify(mockDeliverySearchRepository, times(0)).save(delivery)
    }

    @Test
    @Transactional
    fun deleteDelivery() {
        // Initialize the database
        deliveryService.save(delivery)

        val databaseSizeBeforeDelete = deliveryRepository.findAll().size

        val id = delivery.id
        assertNotNull(id)

        // Delete the delivery
        restDeliveryMockMvc.perform(
            delete("/api/deliveries/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val deliveryList = deliveryRepository.findAll()
        assertThat(deliveryList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Delivery in Elasticsearch
        verify(mockDeliverySearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchDelivery() {
        // InitializesearchDelivery() the database
        deliveryService.save(delivery)
        `when`(mockDeliverySearchRepository.search(queryStringQuery("id:" + delivery.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(listOf(delivery), PageRequest.of(0, 1), 1))
        // Search the delivery
        restDeliveryMockMvc.perform(get("/api/_search/deliveries?query=id:" + delivery.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.id?.toInt())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].postOfficeNumber").value(hasItem(DEFAULT_POST_OFFICE_NUMBER)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
    }

    companion object {

        private const val DEFAULT_ADDRESS = "AAAAAAAAAA"
        private const val UPDATED_ADDRESS = "BBBBBBBBBB"

        private const val DEFAULT_POST_OFFICE_NUMBER: Int = 1
        private const val UPDATED_POST_OFFICE_NUMBER: Int = 2

        private const val DEFAULT_PRICE: Double = 1.0
        private const val UPDATED_PRICE: Double = 2.0

        private val DEFAULT_TYPE: DeliveryType = DeliveryType.POST_OFFICE
        private val UPDATED_TYPE: DeliveryType = DeliveryType.COURIER

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Delivery {
            val delivery = Delivery(
                address = DEFAULT_ADDRESS,
                postOfficeNumber = DEFAULT_POST_OFFICE_NUMBER,
                price = DEFAULT_PRICE,
                type = DEFAULT_TYPE
            )

            // Add required entity
            val order: Order
            if (em.findAll(Order::class).isEmpty()) {
                order = OrderResourceIT.createEntity(em)
                em.persist(order)
                em.flush()
            } else {
                order = em.findAll(Order::class).get(0)
            }
            delivery.order = order
            return delivery
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Delivery {
            val delivery = Delivery(
                address = UPDATED_ADDRESS,
                postOfficeNumber = UPDATED_POST_OFFICE_NUMBER,
                price = UPDATED_PRICE,
                type = UPDATED_TYPE
            )

            // Add required entity
            val order: Order
            if (em.findAll(Order::class).isEmpty()) {
                order = OrderResourceIT.createUpdatedEntity(em)
                em.persist(order)
                em.flush()
            } else {
                order = em.findAll(Order::class).get(0)
            }
            delivery.order = order
            return delivery
        }
    }
}
