package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.ClientCard
import com.team.flowershop.domain.enumeration.CardType
import com.team.flowershop.repository.ClientCardRepository
import com.team.flowershop.repository.search.ClientCardSearchRepository
import com.team.flowershop.service.ClientCardService
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
 * Integration tests for the [ClientCardResource] REST controller.
 *
 * @see ClientCardResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class ClientCardResourceIT {

    @Autowired
    private lateinit var clientCardRepository: ClientCardRepository

    @Autowired
    private lateinit var clientCardService: ClientCardService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.ClientCardSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockClientCardSearchRepository: ClientCardSearchRepository

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

    private lateinit var restClientCardMockMvc: MockMvc

    private lateinit var clientCard: ClientCard

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val clientCardResource = ClientCardResource(clientCardService)
        this.restClientCardMockMvc = MockMvcBuilders.standaloneSetup(clientCardResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        clientCard = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createClientCard() {
        val databaseSizeBeforeCreate = clientCardRepository.findAll().size

        // Create the ClientCard
        restClientCardMockMvc.perform(
            post("/api/client-cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(clientCard))
        ).andExpect(status().isCreated)

        // Validate the ClientCard in the database
        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeCreate + 1)
        val testClientCard = clientCardList[clientCardList.size - 1]
        assertThat(testClientCard.name).isEqualTo(DEFAULT_NAME)
        assertThat(testClientCard.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testClientCard.type).isEqualTo(DEFAULT_TYPE)
        assertThat(testClientCard.bonusAmount).isEqualTo(DEFAULT_BONUS_AMOUNT)
        assertThat(testClientCard.percentage).isEqualTo(DEFAULT_PERCENTAGE)

        // Validate the id for MapsId, the ids must be same
        assertThat(testClientCard.id).isEqualTo(testClientCard.user?.id)

        // Validate the ClientCard in Elasticsearch
        verify(mockClientCardSearchRepository, times(1)).save(testClientCard)
    }

    @Test
    @Transactional
    fun createClientCardWithExistingId() {
        val databaseSizeBeforeCreate = clientCardRepository.findAll().size

        // Create the ClientCard with an existing ID
        clientCard.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientCardMockMvc.perform(
            post("/api/client-cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(clientCard))
        ).andExpect(status().isBadRequest)

        // Validate the ClientCard in the database
        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeCreate)

        // Validate the ClientCard in Elasticsearch
        verify(mockClientCardSearchRepository, times(0)).save(clientCard)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun updateClientCardMapsIdAssociationWithNewId() {
        // Initialize the database
        clientCardService.save(clientCard)
        val databaseSizeBeforeCreate = clientCardRepository.findAll().size

        // Add a new parent entity
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()

        // Load the clientCard
        val updatedClientCard = clientCardRepository.findById(clientCard.id).get()
        // Disconnect from session so that the updates on updatedClientCard are not directly saved in db
        em.detach(updatedClientCard)

        // Update the User with new association value
        updatedClientCard.user = user

        // Update the entity
        restClientCardMockMvc.perform(put("/api/client-cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(updatedClientCard)))
            .andExpect(status().isOk)

        // Validate the ClientCard in the database
        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeCreate)
        val testClientCard = clientCardList.get(clientCardList.size - 1)

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testClientCard.id).isEqualTo(testClientCard.user?.id)

        // Validate the ClientCard in Elasticsearch
        verify(mockClientCardSearchRepository, times(0)).save(clientCard)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = clientCardRepository.findAll().size
        // set the field null
        clientCard.name = null

        // Create the ClientCard, which fails.

        restClientCardMockMvc.perform(
            post("/api/client-cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(clientCard))
        ).andExpect(status().isBadRequest)

        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllClientCards() {
        // Initialize the database
        clientCardRepository.saveAndFlush(clientCard)

        // Get all the clientCardList
        restClientCardMockMvc.perform(get("/api/client-cards?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientCard.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].bonusAmount").value(hasItem(DEFAULT_BONUS_AMOUNT)))
            .andExpect(jsonPath("$.[*].percentage").value(hasItem(DEFAULT_PERCENTAGE)))
    }

    @Test
    @Transactional
    fun getClientCard() {
        // Initialize the database
        clientCardRepository.saveAndFlush(clientCard)

        val id = clientCard.id
        assertNotNull(id)

        // Get the clientCard
        restClientCardMockMvc.perform(get("/api/client-cards/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.bonusAmount").value(DEFAULT_BONUS_AMOUNT))
            .andExpect(jsonPath("$.percentage").value(DEFAULT_PERCENTAGE))
    }

    @Test
    @Transactional
    fun getNonExistingClientCard() {
        // Get the clientCard
        restClientCardMockMvc.perform(get("/api/client-cards/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateClientCard() {
        // Initialize the database
        clientCardService.save(clientCard)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockClientCardSearchRepository)

        val databaseSizeBeforeUpdate = clientCardRepository.findAll().size

        // Update the clientCard
        val id = clientCard.id
        assertNotNull(id)
        val updatedClientCard = clientCardRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedClientCard are not directly saved in db
        em.detach(updatedClientCard)
        updatedClientCard.name = UPDATED_NAME
        updatedClientCard.description = UPDATED_DESCRIPTION
        updatedClientCard.type = UPDATED_TYPE
        updatedClientCard.bonusAmount = UPDATED_BONUS_AMOUNT
        updatedClientCard.percentage = UPDATED_PERCENTAGE

        restClientCardMockMvc.perform(
            put("/api/client-cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedClientCard))
        ).andExpect(status().isOk)

        // Validate the ClientCard in the database
        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeUpdate)
        val testClientCard = clientCardList[clientCardList.size - 1]
        assertThat(testClientCard.name).isEqualTo(UPDATED_NAME)
        assertThat(testClientCard.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testClientCard.type).isEqualTo(UPDATED_TYPE)
        assertThat(testClientCard.bonusAmount).isEqualTo(UPDATED_BONUS_AMOUNT)
        assertThat(testClientCard.percentage).isEqualTo(UPDATED_PERCENTAGE)

        // Validate the ClientCard in Elasticsearch
        verify(mockClientCardSearchRepository, times(1)).save(testClientCard)
    }

    @Test
    @Transactional
    fun updateNonExistingClientCard() {
        val databaseSizeBeforeUpdate = clientCardRepository.findAll().size

        // Create the ClientCard

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientCardMockMvc.perform(
            put("/api/client-cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(clientCard))
        ).andExpect(status().isBadRequest)

        // Validate the ClientCard in the database
        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ClientCard in Elasticsearch
        verify(mockClientCardSearchRepository, times(0)).save(clientCard)
    }

    @Test
    @Transactional
    fun deleteClientCard() {
        // Initialize the database
        clientCardService.save(clientCard)

        val databaseSizeBeforeDelete = clientCardRepository.findAll().size

        val id = clientCard.id
        assertNotNull(id)

        // Delete the clientCard
        restClientCardMockMvc.perform(
            delete("/api/client-cards/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val clientCardList = clientCardRepository.findAll()
        assertThat(clientCardList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the ClientCard in Elasticsearch
        verify(mockClientCardSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchClientCard() {
        // InitializesearchClientCard() the database
        clientCardService.save(clientCard)
        `when`(mockClientCardSearchRepository.search(queryStringQuery("id:" + clientCard.id)))
            .thenReturn(listOf(clientCard))
        // Search the clientCard
        restClientCardMockMvc.perform(get("/api/_search/client-cards?query=id:" + clientCard.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientCard.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].bonusAmount").value(hasItem(DEFAULT_BONUS_AMOUNT)))
            .andExpect(jsonPath("$.[*].percentage").value(hasItem(DEFAULT_PERCENTAGE)))
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_TYPE: CardType = CardType.BONUS
        private val UPDATED_TYPE: CardType = CardType.SOCIAL

        private const val DEFAULT_BONUS_AMOUNT: Double = 1.0
        private const val UPDATED_BONUS_AMOUNT: Double = 2.0

        private const val DEFAULT_PERCENTAGE: Double = 1.0
        private const val UPDATED_PERCENTAGE: Double = 2.0

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ClientCard {
            val clientCard = ClientCard(
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION,
                type = DEFAULT_TYPE,
                bonusAmount = DEFAULT_BONUS_AMOUNT,
                percentage = DEFAULT_PERCENTAGE
            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            clientCard.user = user
            return clientCard
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ClientCard {
            val clientCard = ClientCard(
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION,
                type = UPDATED_TYPE,
                bonusAmount = UPDATED_BONUS_AMOUNT,
                percentage = UPDATED_PERCENTAGE
            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            clientCard.user = user
            return clientCard
        }
    }
}
