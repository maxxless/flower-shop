package com.team.flowershop.service

import com.team.flowershop.config.ANONYMOUS_USER
import com.team.flowershop.config.DEFAULT_LANGUAGE
import com.team.flowershop.domain.Authority
import com.team.flowershop.domain.Cart
import com.team.flowershop.domain.ClientCard
import com.team.flowershop.domain.User
import com.team.flowershop.domain.enumeration.CardType
import com.team.flowershop.repository.AuthorityRepository
import com.team.flowershop.repository.CartRepository
import com.team.flowershop.repository.ClientCardRepository
import com.team.flowershop.repository.UserRepository
import com.team.flowershop.repository.search.UserSearchRepository
import com.team.flowershop.security.USER
import com.team.flowershop.security.getCurrentUserLogin
import com.team.flowershop.service.dto.UserDTO
import io.github.jhipster.security.RandomUtil
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for managing users.
 */
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userSearchRepository: UserSearchRepository,
    private val authorityRepository: AuthorityRepository,
    private val clientCardRepository: ClientCardRepository,
    private val cartRepository: CartRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun activateRegistration(key: String): Optional<User> {
        log.debug("Activating user for activation key {}", key)
        return userRepository.findOneByActivationKey(key)
            .map { user ->
                // activate given user for the registration key.
                user.activated = true
                user.activationKey = null
                userSearchRepository.save(user)

                val cart = Cart(totalPriceWithoutDiscount = 0.0, cardDiscount = 0.0,
                    bonusDiscount = 0.0, finalPrice = 0.0, user = user)
                cartRepository.save(cart)

                val card = ClientCard(user = user, name = "Бонусна карта #${user.id}", description = "Картка видана за реєстрацію користувачеві ${user.email}", type = CardType.BONUS, bonusAmount = 0.0, percentage = 0.0)
                clientCardRepository.save(card)

                log.debug("Activated user: {}", user)
                user
            }
    }

    fun completePasswordReset(newPassword: String, key: String): Optional<User> {
        log.debug("Reset user password for reset key {}", key)
        return userRepository.findOneByResetKey(key)
            .filter { user -> user.resetDate?.isAfter(Instant.now().minusSeconds(86400)) ?: false }
            .map { user ->
                user.password = passwordEncoder.encode(newPassword)
                user.resetKey = null
                user.resetDate = null
                user
            }
    }

    fun requestPasswordReset(mail: String): Optional<User> {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::activated)
            .map { user ->
                user.resetKey = RandomUtil.generateResetKey()
                user.resetDate = Instant.now()
                user
            }
    }

    fun registerUser(userDTO: UserDTO, password: String): User {
        val login = userDTO.login ?: throw IllegalArgumentException("Empty login not allowed")
        val email = userDTO.email
        userRepository.findOneByLogin(login.toLowerCase()).ifPresent { existingUser ->
            val removed = removeNonActivatedUser(existingUser)
            if (!removed) {
                throw UsernameAlreadyUsedException()
            }
        }
        userRepository.findOneByEmailIgnoreCase(email).ifPresent { existingUser ->
            val removed = removeNonActivatedUser(existingUser)
            if (!removed) {
                throw EmailAlreadyUsedException()
            }
        }
        val newUser = User()
        val encryptedPassword = passwordEncoder.encode(password)
        newUser.apply {
            this.login = login.toLowerCase()
            // new user gets initially a generated password
            this.password = encryptedPassword
            firstName = userDTO.firstName
            lastName = userDTO.lastName
            this.email = email?.toLowerCase()
            imageUrl = userDTO.imageUrl
            langKey = userDTO.langKey
            // new user is not active
            activated = false
            // new user gets registration key
            activationKey = RandomUtil.generateActivationKey()
            authorities = mutableSetOf()
            authorityRepository.findById(USER).ifPresent { authorities.add(it) }
        }
        userRepository.save(newUser)
        userSearchRepository.save(newUser)
        log.debug("Created Information for User: {}", newUser)
        return newUser
    }

    private fun removeNonActivatedUser(existingUser: User): Boolean {
        if (existingUser.activated) {
            return false
        }
        userRepository.delete(existingUser)
        userRepository.flush()
        return true
    }

    fun createUser(userDTO: UserDTO): User {
        val encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword())
        val user = User(
            login = userDTO.login?.toLowerCase(),
            firstName = userDTO.firstName,
            lastName = userDTO.lastName,
            email = userDTO.email?.toLowerCase(),
            imageUrl = userDTO.imageUrl,
            langKey = userDTO.langKey ?: DEFAULT_LANGUAGE, // default language
            password = encryptedPassword,
            resetKey = RandomUtil.generateResetKey(),
            resetDate = Instant.now(),
            activated = true
        )
        userDTO.authorities?.apply {
            val authorities = this.asSequence()
                .map(authorityRepository::findById)
                .filter(Optional<Authority>::isPresent)
                .mapTo(mutableSetOf()) { it.get() }
            user.authorities = authorities
        }
        userRepository.save(user)
        userSearchRepository.save(user)
        log.debug("Created Information for User: {}", user)
        return user
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName last name of user.
     * @param email email id of user.
     * @param langKey language key.
     * @param imageUrl image URL of user.
     */
    fun updateUser(firstName: String?, lastName: String?, email: String?, langKey: String?, imageUrl: String?) {
        getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent { user ->
                user.firstName = firstName
                user.lastName = lastName
                user.email = email?.toLowerCase()
                user.langKey = langKey
                user.imageUrl = imageUrl
                userSearchRepository.save(user)
                log.debug("Changed Information for User: {}", user)
            }
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    fun updateUser(userDTO: UserDTO): Optional<UserDTO> {
        return Optional.of(userRepository.findById(userDTO.id!!))
            .filter(Optional<User>::isPresent)
            .map { it.get() }
            .map { user ->
                user.apply {
                    login = userDTO.login!!.toLowerCase()
                    firstName = userDTO.firstName
                    lastName = userDTO.lastName
                    email = userDTO.email?.toLowerCase()
                    imageUrl = userDTO.imageUrl
                    activated = userDTO.activated
                    langKey = userDTO.langKey
                }
                val managedAuthorities = user.authorities
                managedAuthorities.clear()
                userDTO.authorities?.apply {
                    this.asSequence()
                        .map { authorityRepository.findById(it) }
                        .filter { it.isPresent }
                        .mapTo(managedAuthorities) { it.get() }
                }
                userSearchRepository.save(user)
                log.debug("Changed Information for User: {}", user)
                user
            }
            .map { UserDTO(it) }
    }

    fun deleteUser(login: String) {
        userRepository.findOneByLogin(login).ifPresent { user ->
            userRepository.delete(user)
            userSearchRepository.delete(user)
            log.debug("Deleted User: {}", user)
        }
    }

    fun changePassword(currentClearTextPassword: String, newPassword: String) {
        getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent { user ->
                val currentEncryptedPassword = user.password
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw InvalidPasswordException()
                }
                val encryptedPassword = passwordEncoder.encode(newPassword)
                user.password = encryptedPassword
                log.debug("Changed password for User: {}", user)
            }
    }

    @Transactional(readOnly = true)
    fun getAllManagedUsers(pageable: Pageable): Page<UserDTO> =
        userRepository.findAllByLoginNot(pageable, ANONYMOUS_USER).map { UserDTO(it) }

    @Transactional(readOnly = true)
    fun getUserWithAuthoritiesByLogin(login: String): Optional<User> =
        userRepository.findOneWithAuthoritiesByLogin(login)

    @Suppress("unused")
    @Transactional(readOnly = true)
    fun getUserWithAuthorities(id: Long): Optional<User> =
        userRepository.findOneWithAuthoritiesById(id)

    @Transactional(readOnly = true)
    fun getUserWithAuthorities(): Optional<User> =
        getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin)

    /**
     * Not activated users should be automatically deleted after 3 days.
     *
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    fun removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                Instant.now().minus(3, ChronoUnit.DAYS)
            )
            .forEach { user ->
                log.debug("Deleting not activated user {}", user.login)
                userRepository.delete(user)
                userSearchRepository.delete(user)
            }
    }

    /**
     * @return a list of all the authorities
     */
    fun getAuthorities() =
        authorityRepository.findAll().asSequence().map { it.name }.filterNotNullTo(mutableListOf())
}
