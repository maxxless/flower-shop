package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Cart
import com.team.flowershop.repository.CartRepository
import com.team.flowershop.repository.search.CartSearchRepository
import com.team.flowershop.service.CartService
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
 * Integration tests for the [CartResource] REST controller.
 *
 * @see CartResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class CartResourceIT {

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var cartService: CartService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.CartSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockCartSearchRepository: CartSearchRepository

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

    private lateinit var restCartMockMvc: MockMvc

    private lateinit var cart: Cart

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val cartResource = CartResource(cartService)
        this.restCartMockMvc = MockMvcBuilders.standaloneSetup(cartResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        cart = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCart() {
        val databaseSizeBeforeCreate = cartRepository.findAll().size

        // Create the Cart
        restCartMockMvc.perform(
            post("/api/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(cart))
        ).andExpect(status().isCreated)

        // Validate the Cart in the database
        val cartList = cartRepository.findAll()
        assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1)
        val testCart = cartList[cartList.size - 1]
        assertThat(testCart.totalPriceWithoutDiscount).isEqualTo(DEFAULT_TOTAL_PRICE_WITHOUT_DISCOUNT)
        assertThat(testCart.cardDiscount).isEqualTo(DEFAULT_CARD_DISCOUNT)
        assertThat(testCart.bonusDiscount).isEqualTo(DEFAULT_BONUS_DISCOUNT)
        assertThat(testCart.finalPrice).isEqualTo(DEFAULT_FINAL_PRICE)

        // Validate the id for MapsId, the ids must be same
        assertThat(testCart.id).isEqualTo(testCart.user?.id)

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(1)).save(testCart)
    }

    @Test
    @Transactional
    fun createCartWithExistingId() {
        val databaseSizeBeforeCreate = cartRepository.findAll().size

        // Create the Cart with an existing ID
        cart.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartMockMvc.perform(
            post("/api/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(cart))
        ).andExpect(status().isBadRequest)

        // Validate the Cart in the database
        val cartList = cartRepository.findAll()
        assertThat(cartList).hasSize(databaseSizeBeforeCreate)

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(0)).save(cart)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun updateCartMapsIdAssociationWithNewId() {
        // Initialize the database
        cartService.save(cart)
        val databaseSizeBeforeCreate = cartRepository.findAll().size

        // Add a new parent entity
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()

        // Load the cart
        val updatedCart = cartRepository.findById(cart.id).get()
        // Disconnect from session so that the updates on updatedCart are not directly saved in db
        em.detach(updatedCart)

        // Update the User with new association value
        updatedCart.user = user

        // Update the entity
        restCartMockMvc.perform(put("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(updatedCart)))
            .andExpect(status().isOk)

        // Validate the Cart in the database
        val cartList = cartRepository.findAll()
        assertThat(cartList).hasSize(databaseSizeBeforeCreate)
        val testCart = cartList.get(cartList.size - 1)

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testCart.id).isEqualTo(testCart.user?.id)

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(0)).save(cart)
    }

    @Test
    @Transactional
    fun getAllCarts() {
        // Initialize the database
        cartRepository.saveAndFlush(cart)

        // Get all the cartList
        restCartMockMvc.perform(get("/api/carts?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.id?.toInt())))
            .andExpect(jsonPath("$.[*].totalPriceWithoutDiscount").value(hasItem(DEFAULT_TOTAL_PRICE_WITHOUT_DISCOUNT)))
            .andExpect(jsonPath("$.[*].cardDiscount").value(hasItem(DEFAULT_CARD_DISCOUNT)))
            .andExpect(jsonPath("$.[*].bonusDiscount").value(hasItem(DEFAULT_BONUS_DISCOUNT)))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(DEFAULT_FINAL_PRICE)))
    }

    @Test
    @Transactional
    fun getCart() {
        // Initialize the database
        cartRepository.saveAndFlush(cart)

        val id = cart.id
        assertNotNull(id)

        // Get the cart
        restCartMockMvc.perform(get("/api/carts/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.totalPriceWithoutDiscount").value(DEFAULT_TOTAL_PRICE_WITHOUT_DISCOUNT))
            .andExpect(jsonPath("$.cardDiscount").value(DEFAULT_CARD_DISCOUNT))
            .andExpect(jsonPath("$.bonusDiscount").value(DEFAULT_BONUS_DISCOUNT))
            .andExpect(jsonPath("$.finalPrice").value(DEFAULT_FINAL_PRICE))
    }

    @Test
    @Transactional
    fun getNonExistingCart() {
        // Get the cart
        restCartMockMvc.perform(get("/api/carts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCart() {
        // Initialize the database
        cartService.save(cart)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCartSearchRepository)

        val databaseSizeBeforeUpdate = cartRepository.findAll().size

        // Update the cart
        val id = cart.id
        assertNotNull(id)
        val updatedCart = cartRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCart are not directly saved in db
        em.detach(updatedCart)
        updatedCart.totalPriceWithoutDiscount = UPDATED_TOTAL_PRICE_WITHOUT_DISCOUNT
        updatedCart.cardDiscount = UPDATED_CARD_DISCOUNT
        updatedCart.bonusDiscount = UPDATED_BONUS_DISCOUNT
        updatedCart.finalPrice = UPDATED_FINAL_PRICE

        restCartMockMvc.perform(
            put("/api/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCart))
        ).andExpect(status().isOk)

        // Validate the Cart in the database
        val cartList = cartRepository.findAll()
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate)
        val testCart = cartList[cartList.size - 1]
        assertThat(testCart.totalPriceWithoutDiscount).isEqualTo(UPDATED_TOTAL_PRICE_WITHOUT_DISCOUNT)
        assertThat(testCart.cardDiscount).isEqualTo(UPDATED_CARD_DISCOUNT)
        assertThat(testCart.bonusDiscount).isEqualTo(UPDATED_BONUS_DISCOUNT)
        assertThat(testCart.finalPrice).isEqualTo(UPDATED_FINAL_PRICE)

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(1)).save(testCart)
    }

    @Test
    @Transactional
    fun updateNonExistingCart() {
        val databaseSizeBeforeUpdate = cartRepository.findAll().size

        // Create the Cart

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartMockMvc.perform(
            put("/api/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(cart))
        ).andExpect(status().isBadRequest)

        // Validate the Cart in the database
        val cartList = cartRepository.findAll()
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(0)).save(cart)
    }

    @Test
    @Transactional
    fun deleteCart() {
        // Initialize the database
        cartService.save(cart)

        val databaseSizeBeforeDelete = cartRepository.findAll().size

        val id = cart.id
        assertNotNull(id)

        // Delete the cart
        restCartMockMvc.perform(
            delete("/api/carts/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val cartList = cartRepository.findAll()
        assertThat(cartList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Cart in Elasticsearch
        verify(mockCartSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchCart() {
        // InitializesearchCart() the database
        cartService.save(cart)
        `when`(mockCartSearchRepository.search(queryStringQuery("id:" + cart.id)))
            .thenReturn(listOf(cart))
        // Search the cart
        restCartMockMvc.perform(get("/api/_search/carts?query=id:" + cart.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cart.id?.toInt())))
            .andExpect(jsonPath("$.[*].totalPriceWithoutDiscount").value(hasItem(DEFAULT_TOTAL_PRICE_WITHOUT_DISCOUNT)))
            .andExpect(jsonPath("$.[*].cardDiscount").value(hasItem(DEFAULT_CARD_DISCOUNT)))
            .andExpect(jsonPath("$.[*].bonusDiscount").value(hasItem(DEFAULT_BONUS_DISCOUNT)))
            .andExpect(jsonPath("$.[*].finalPrice").value(hasItem(DEFAULT_FINAL_PRICE)))
    }

    companion object {

        private const val DEFAULT_TOTAL_PRICE_WITHOUT_DISCOUNT: Double = 1.0
        private const val UPDATED_TOTAL_PRICE_WITHOUT_DISCOUNT: Double = 2.0

        private const val DEFAULT_CARD_DISCOUNT: Double = 1.0
        private const val UPDATED_CARD_DISCOUNT: Double = 2.0

        private const val DEFAULT_BONUS_DISCOUNT: Double = 1.0
        private const val UPDATED_BONUS_DISCOUNT: Double = 2.0

        private const val DEFAULT_FINAL_PRICE: Double = 1.0
        private const val UPDATED_FINAL_PRICE: Double = 2.0

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Cart {
            val cart = Cart(
                totalPriceWithoutDiscount = DEFAULT_TOTAL_PRICE_WITHOUT_DISCOUNT,
                cardDiscount = DEFAULT_CARD_DISCOUNT,
                bonusDiscount = DEFAULT_BONUS_DISCOUNT,
                finalPrice = DEFAULT_FINAL_PRICE
            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            cart.user = user
            return cart
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Cart {
            val cart = Cart(
                totalPriceWithoutDiscount = UPDATED_TOTAL_PRICE_WITHOUT_DISCOUNT,
                cardDiscount = UPDATED_CARD_DISCOUNT,
                bonusDiscount = UPDATED_BONUS_DISCOUNT,
                finalPrice = UPDATED_FINAL_PRICE
            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            cart.user = user
            return cart
        }
    }
}
