package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Colour
import com.team.flowershop.repository.ColourRepository
import com.team.flowershop.repository.search.ColourSearchRepository
import com.team.flowershop.service.ColourService
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
 * Integration tests for the [ColourResource] REST controller.
 *
 * @see ColourResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class ColourResourceIT {

    @Autowired
    private lateinit var colourRepository: ColourRepository

    @Autowired
    private lateinit var colourService: ColourService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.ColourSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockColourSearchRepository: ColourSearchRepository

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

    private lateinit var restColourMockMvc: MockMvc

    private lateinit var colour: Colour

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val colourResource = ColourResource(colourService)
        this.restColourMockMvc = MockMvcBuilders.standaloneSetup(colourResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        colour = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createColour() {
        val databaseSizeBeforeCreate = colourRepository.findAll().size

        // Create the Colour
        restColourMockMvc.perform(
            post("/api/colours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(colour))
        ).andExpect(status().isCreated)

        // Validate the Colour in the database
        val colourList = colourRepository.findAll()
        assertThat(colourList).hasSize(databaseSizeBeforeCreate + 1)
        val testColour = colourList[colourList.size - 1]
        assertThat(testColour.name).isEqualTo(DEFAULT_NAME)

        // Validate the Colour in Elasticsearch
        verify(mockColourSearchRepository, times(1)).save(testColour)
    }

    @Test
    @Transactional
    fun createColourWithExistingId() {
        val databaseSizeBeforeCreate = colourRepository.findAll().size

        // Create the Colour with an existing ID
        colour.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restColourMockMvc.perform(
            post("/api/colours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(colour))
        ).andExpect(status().isBadRequest)

        // Validate the Colour in the database
        val colourList = colourRepository.findAll()
        assertThat(colourList).hasSize(databaseSizeBeforeCreate)

        // Validate the Colour in Elasticsearch
        verify(mockColourSearchRepository, times(0)).save(colour)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = colourRepository.findAll().size
        // set the field null
        colour.name = null

        // Create the Colour, which fails.

        restColourMockMvc.perform(
            post("/api/colours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(colour))
        ).andExpect(status().isBadRequest)

        val colourList = colourRepository.findAll()
        assertThat(colourList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllColours() {
        // Initialize the database
        colourRepository.saveAndFlush(colour)

        // Get all the colourList
        restColourMockMvc.perform(get("/api/colours?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colour.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    fun getColour() {
        // Initialize the database
        colourRepository.saveAndFlush(colour)

        val id = colour.id
        assertNotNull(id)

        // Get the colour
        restColourMockMvc.perform(get("/api/colours/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }

    @Test
    @Transactional
    fun getNonExistingColour() {
        // Get the colour
        restColourMockMvc.perform(get("/api/colours/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateColour() {
        // Initialize the database
        colourService.save(colour)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockColourSearchRepository)

        val databaseSizeBeforeUpdate = colourRepository.findAll().size

        // Update the colour
        val id = colour.id
        assertNotNull(id)
        val updatedColour = colourRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedColour are not directly saved in db
        em.detach(updatedColour)
        updatedColour.name = UPDATED_NAME

        restColourMockMvc.perform(
            put("/api/colours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedColour))
        ).andExpect(status().isOk)

        // Validate the Colour in the database
        val colourList = colourRepository.findAll()
        assertThat(colourList).hasSize(databaseSizeBeforeUpdate)
        val testColour = colourList[colourList.size - 1]
        assertThat(testColour.name).isEqualTo(UPDATED_NAME)

        // Validate the Colour in Elasticsearch
        verify(mockColourSearchRepository, times(1)).save(testColour)
    }

    @Test
    @Transactional
    fun updateNonExistingColour() {
        val databaseSizeBeforeUpdate = colourRepository.findAll().size

        // Create the Colour

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColourMockMvc.perform(
            put("/api/colours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(colour))
        ).andExpect(status().isBadRequest)

        // Validate the Colour in the database
        val colourList = colourRepository.findAll()
        assertThat(colourList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Colour in Elasticsearch
        verify(mockColourSearchRepository, times(0)).save(colour)
    }

    @Test
    @Transactional
    fun deleteColour() {
        // Initialize the database
        colourService.save(colour)

        val databaseSizeBeforeDelete = colourRepository.findAll().size

        val id = colour.id
        assertNotNull(id)

        // Delete the colour
        restColourMockMvc.perform(
            delete("/api/colours/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val colourList = colourRepository.findAll()
        assertThat(colourList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Colour in Elasticsearch
        verify(mockColourSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchColour() {
        // InitializesearchColour() the database
        colourService.save(colour)
        `when`(mockColourSearchRepository.search(queryStringQuery("id:" + colour.id)))
            .thenReturn(listOf(colour))
        // Search the colour
        restColourMockMvc.perform(get("/api/_search/colours?query=id:" + colour.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(colour.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Colour {
            val colour = Colour(
                name = DEFAULT_NAME
            )

            return colour
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Colour {
            val colour = Colour(
                name = UPDATED_NAME
            )

            return colour
        }
    }
}
