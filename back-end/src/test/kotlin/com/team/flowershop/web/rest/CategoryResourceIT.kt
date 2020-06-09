package com.team.flowershop.web.rest

import com.team.flowershop.FlowershopApp
import com.team.flowershop.domain.Category
import com.team.flowershop.repository.CategoryRepository
import com.team.flowershop.repository.search.CategorySearchRepository
import com.team.flowershop.service.CategoryService
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
 * Integration tests for the [CategoryResource] REST controller.
 *
 * @see CategoryResource
 */
@SpringBootTest(classes = [FlowershopApp::class])
class CategoryResourceIT {

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var categoryService: CategoryService

    /**
     * This repository is mocked in the com.team.flowershop.repository.search test package.
     *
     * @see com.team.flowershop.repository.search.CategorySearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockCategorySearchRepository: CategorySearchRepository

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

    private lateinit var restCategoryMockMvc: MockMvc

    private lateinit var category: Category

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val categoryResource = CategoryResource(categoryService)
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        category = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCategory() {
        val databaseSizeBeforeCreate = categoryRepository.findAll().size

        // Create the Category
        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(category))
        ).andExpect(status().isCreated)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1)
        val testCategory = categoryList[categoryList.size - 1]
        assertThat(testCategory.name).isEqualTo(DEFAULT_NAME)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(1)).save(testCategory)
    }

    @Test
    @Transactional
    fun createCategoryWithExistingId() {
        val databaseSizeBeforeCreate = categoryRepository.findAll().size

        // Create the Category with an existing ID
        category.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(0)).save(category)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = categoryRepository.findAll().size
        // set the field null
        category.name = null

        // Create the Category, which fails.

        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllCategories() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    fun getCategory() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val id = category.id
        assertNotNull(id)

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }

    @Test
    @Transactional
    fun getNonExistingCategory() {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCategory() {
        // Initialize the database
        categoryService.save(category)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCategorySearchRepository)

        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

        // Update the category
        val id = category.id
        assertNotNull(id)
        val updatedCategory = categoryRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        em.detach(updatedCategory)
        updatedCategory.name = UPDATED_NAME

        restCategoryMockMvc.perform(
            put("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCategory))
        ).andExpect(status().isOk)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
        val testCategory = categoryList[categoryList.size - 1]
        assertThat(testCategory.name).isEqualTo(UPDATED_NAME)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(1)).save(testCategory)
    }

    @Test
    @Transactional
    fun updateNonExistingCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

        // Create the Category

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            put("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(0)).save(category)
    }

    @Test
    @Transactional
    fun deleteCategory() {
        // Initialize the database
        categoryService.save(category)

        val databaseSizeBeforeDelete = categoryRepository.findAll().size

        val id = category.id
        assertNotNull(id)

        // Delete the category
        restCategoryMockMvc.perform(
            delete("/api/categories/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchCategory() {
        // InitializesearchCategory() the database
        categoryService.save(category)
        `when`(mockCategorySearchRepository.search(queryStringQuery("id:" + category.id)))
            .thenReturn(listOf(category))
        // Search the category
        restCategoryMockMvc.perform(get("/api/_search/categories?query=id:" + category.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
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
        fun createEntity(em: EntityManager): Category {
            val category = Category(
                name = DEFAULT_NAME
            )

            return category
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Category {
            val category = Category(
                name = UPDATED_NAME
            )

            return category
        }
    }
}
