package com.team.flowershop.web.rest

import com.team.flowershop.config.LOGIN_REGEX
import com.team.flowershop.domain.*
import com.team.flowershop.domain.enumeration.CardType
import com.team.flowershop.domain.enumeration.DeliveryType
import com.team.flowershop.repository.*
import com.team.flowershop.security.getCurrentUserLogin
import com.team.flowershop.service.MailService
import com.team.flowershop.service.UserService
import com.team.flowershop.service.dto.PasswordChangeDTO
import com.team.flowershop.service.dto.UserDTO
import com.team.flowershop.web.rest.errors.EmailAlreadyUsedException
import com.team.flowershop.web.rest.errors.InvalidPasswordException
import com.team.flowershop.web.rest.errors.LoginAlreadyUsedException
import com.team.flowershop.web.rest.vm.KeyAndPasswordVM
import com.team.flowershop.web.rest.vm.ManagedUserVM
import java.time.Instant
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
class AccountResource(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val mailService: MailService,
    // custom
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val deliveryRepository: DeliveryRepository,
    private val flowerRepository: FlowerRepository,
    private val collectionRepository: CollectionRepository,
    private val packingRepository: PackingRepository,
    private val colourRepository: ColourRepository,
    private val clientCardRepository: ClientCardRepository,
    // composites
    private val collectionInCartRepository: CollectionInCartRepository,
    private val collectionInOrderRepository: CollectionInOrderRepository,
    private val flowerInCartRepository: FlowerInCartRepository,
    private val flowerInOrderRepository: FlowerInOrderRepository
) {

    internal class AccountResourceException(message: String) : RuntimeException(message)

    class UserAccountDTO(
        var id: Long? = null,

        @field:NotBlank
        @field:Pattern(regexp = LOGIN_REGEX)
        @field:Size(min = 1, max = 50)
        var login: String? = null,

        @field:Size(max = 50)
        var firstName: String? = null,

        @field:Size(max = 50)
        var lastName: String? = null,

        @field:Email
        @field:Size(min = 5, max = 254)
        var email: String? = null,

        var clientCard: ClientCard? = null,

        var cart: Cart? = null
    ) {
        constructor(user: User) :
            this(
                user.id, user.login, user.firstName, user.lastName, user.email, user.clientCard, user.cart
            )
    }

    class FlowerToCartDTO(val amount: Int, val colourId: Long)

    class CollectionToCartDTO(val amount: Int, val packingId: Long)

    class FromCartToOrderDTO(val packingId: Long, val address: String? = null, val postOfficeNumber: Int? = null, val deliveryType: DeliveryType)

    private val log = LoggerFactory.getLogger(javaClass)

    /** ############################################### */

    @PostMapping("/order")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun makeOrder(@RequestBody dto: FromCartToOrderDTO) {
        val user = userService.getUserWithAuthorities().get()
        val userCard = user.clientCard!!
        val cart = user.cart!!

        // order details
        val packaging = packingRepository.findById(dto.packingId).get()
        val order = orderRepository.save(Order(totalPrice = cart.finalPrice!! + packaging.price!!,
        date = Instant.now(), user = user, packing = packaging))

        for (flower in cart.flowerDetails) {
            flowerInOrderRepository.save(FlowerInOrder(amount = flower.amount,
            colour = flower.colour, flower = flower.flower, order = order))
        }

        for (collection in cart.collectionDetails) {
            collectionInOrderRepository.save(CollectionInOrder(amount = collection.amount,
            collection = collection.collection, packing = collection.packing, order = order))
        }

        // delivery details
        val deliveryType = dto.deliveryType
        val address = if (deliveryType == DeliveryType.SELF_PICK_UP) {
            "м.Львів, вул. Політехнічна, 4"
        } else {
            dto.address
        }
        val postOfficeNumber = if (deliveryType == DeliveryType.POST_OFFICE) {
            dto.postOfficeNumber
        } else {
            null
        }
        var priceForDelivery = when (deliveryType) {
            DeliveryType.SELF_PICK_UP -> {
                0.0
            }
            DeliveryType.POST_OFFICE -> {
                40.0
            }
            else -> {
                60.0
            }
        }
        if (userCard.type == CardType.GOLD) {
            priceForDelivery = 0.0
        }
        val delivery = deliveryRepository.save(Delivery(address = address, postOfficeNumber = postOfficeNumber,
            price = priceForDelivery, type = deliveryType, order = order, user = user))

        // add bonuses and check if new card could be given
        if (userCard.type == CardType.BONUS && user.orders.size > 2) {
            userCard.type = CardType.SOCIAL
            userCard.percentage = 5.0
            userCard.description = "Картка видана за три успішні замовлення"
        } else if (userCard.type == CardType.SOCIAL && user.orders.size > 4) {
            userCard.type = CardType.GOLD
            userCard.percentage = 7.0
            userCard.description = "Картка видана за п'ять успішних замовлень"
        }
        userCard.bonusAmount = order.totalPrice!! / 10
        clientCardRepository.save(userCard)

        // clear the cart
        collectionInCartRepository.deleteAll(cart.collectionDetails)
        flowerInCartRepository.deleteAll(cart.flowerDetails)

        cart.totalPriceWithoutDiscount = 0.0
        cart.cardDiscount = 0.0
        cart.bonusDiscount = 0.0
        cart.finalPrice = 0.0
        cartRepository.save(cart)

        mailService.sendOrderMail(user, order, delivery)
    }

    @PostMapping("/flowers/{flowerId}/cart")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun addFlowerToCart(@PathVariable flowerId: Long, @RequestBody dto: FlowerToCartDTO) {
        val user = userService.getUserWithAuthorities().get()
        val cart = user.cart!!
        val userCard = user.clientCard!!

        val flower = flowerRepository.findById(flowerId).get()
        val colour = colourRepository.findById(dto.colourId).get()

        val flowerInCart = flowerInCartRepository.save(FlowerInCart(amount = dto.amount, colour = colour, flower = flower, cart = cart))

        cart.flowerDetails.add(flowerInCart)
        cart.totalPriceWithoutDiscount = cart.totalPriceWithoutDiscount!! + dto.amount * flower.price!!
        if (userCard.type != CardType.BONUS) {
            cart.cardDiscount = cart.totalPriceWithoutDiscount!! * userCard.percentage!! / 100
        }
        cart.bonusDiscount = userCard.bonusAmount
        cart.finalPrice = cart.totalPriceWithoutDiscount!! - cart.cardDiscount!! - cart.bonusDiscount!!
        cartRepository.save(cart)
    }

    @DeleteMapping("/flowers/{flowerInCartId}/cart")
    @Transactional
    fun removeFlowerFromCart(@PathVariable flowerInCartId: Long) {
        val user = userService.getUserWithAuthorities().get()
        val cart = user.cart!!
        val userCard = user.clientCard!!

        val flowerInCart = flowerInCartRepository.findById(flowerInCartId).get()

        cart.totalPriceWithoutDiscount = cart.totalPriceWithoutDiscount!! - flowerInCart.amount!! * flowerInCart.flower?.price!!
        if (userCard.type != CardType.BONUS) {
            cart.cardDiscount = cart.totalPriceWithoutDiscount!! * userCard.percentage!! / 100
        }
        cart.bonusDiscount = userCard.bonusAmount
        cart.finalPrice = cart.totalPriceWithoutDiscount!! - cart.cardDiscount!! - cart.bonusDiscount!!
        cartRepository.save(cart)

        flowerInCartRepository.delete(flowerInCart)
    }

    @PostMapping("/collections/{collectionId}/cart")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun addCollectionToCart(@PathVariable collectionId: Long, @RequestBody dto: CollectionToCartDTO) {
        val user = userService.getUserWithAuthorities().get()
        val cart = user.cart!!
        val userCard = user.clientCard!!

        val collection = collectionRepository.findById(collectionId).get()
        val packing = packingRepository.findById(dto.packingId).get()

        val collectionInCart = collectionInCartRepository.save(CollectionInCart(amount = dto.amount, collection = collection, packing = packing, cart = cart))

        cart.collectionDetails.add(collectionInCart)
        cart.totalPriceWithoutDiscount = cart.totalPriceWithoutDiscount!! + dto.amount * collection.price!! + packing.price!!
        if (userCard.type != CardType.BONUS) {
            cart.cardDiscount = cart.totalPriceWithoutDiscount!! * userCard.percentage!! / 100
        }
        cart.bonusDiscount = userCard.bonusAmount
        cart.finalPrice = cart.totalPriceWithoutDiscount!! - cart.cardDiscount!! - cart.bonusDiscount!!
        cartRepository.save(cart)
    }

    @DeleteMapping("/collections/{collectionInCartId}/cart")
    @Transactional
    fun removeCollectionFromCart(@PathVariable collectionInCartId: Long) {
        val user = userService.getUserWithAuthorities().get()
        val cart = user.cart!!
        val userCard = user.clientCard!!

        val collectionInCart = collectionInCartRepository.findById(collectionInCartId).get()

        cart.totalPriceWithoutDiscount = cart.totalPriceWithoutDiscount!! - collectionInCart.amount!! * collectionInCart.collection?.price!! - collectionInCart.packing?.price!!
        if (userCard.type != CardType.BONUS) {
            cart.cardDiscount = cart.totalPriceWithoutDiscount!! * userCard.percentage!! / 100
        }
        cart.bonusDiscount = userCard.bonusAmount
        cart.finalPrice = cart.totalPriceWithoutDiscount!! - cart.cardDiscount!! - cart.bonusDiscount!!
        cartRepository.save(cart)

        collectionInCartRepository.delete(collectionInCart)
    }

    @GetMapping("/account/orders")
    @Transactional
    fun getAccountOrders(): Set<Order> =
        userService.getUserWithAuthorities()
            .map { it.orders }
            .orElseThrow { AccountResourceException("User could not be found") }

    @GetMapping("/account/deliveries")
    @Transactional
    fun getAccountDeliveries(): Set<Delivery> =
        userService.getUserWithAuthorities()
            .map { it.deliveries }
            .orElseThrow { AccountResourceException("User could not be found") }

    /** ############################################### */

    /**
     * `POST  /register` : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException `400 (Bad Request)` if the password is incorrect.
     * @throws EmailAlreadyUsedException `400 (Bad Request)` if the email is already used.
     * @throws LoginAlreadyUsedException `400 (Bad Request)` if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAccount(@Valid @RequestBody managedUserVM: ManagedUserVM) {
        if (!checkPasswordLength(managedUserVM.password)) {
            throw InvalidPasswordException()
        }
        val user = userService.registerUser(managedUserVM, managedUserVM.password!!)
        mailService.sendActivationEmail(user)
    }

    /**
     * `GET  /activate` : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException `500 (Internal Server Error)` if the user couldn't be activated.
     */
    @GetMapping("/activate")
    fun activateAccount(@RequestParam(value = "key") key: String) {
        val user = userService.activateRegistration(key)
        if (!user.isPresent) {
            throw AccountResourceException("No user was found for this activation key")
        }
    }

    /**
     * `GET  /authenticate` : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    fun isAuthenticated(request: HttpServletRequest): String? {
        log.debug("REST request to check if the current user is authenticated")
        return request.remoteUser
    }

    /**
     * `GET  /account` : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException `500 (Internal Server Error)` if the user couldn't be returned.
     */
    @GetMapping("/account")
    fun getAccount(): UserDTO =
        userService.getUserWithAuthorities()
            .map { UserDTO(it) }
            .orElseThrow { AccountResourceException("User could not be found") }

    /**
     * `GET  /account/details` : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException `500 (Internal Server Error)` if the user couldn't be returned.
     */
    @GetMapping("/account/details")
    fun getAccountDetails(): UserAccountDTO =
        userService.getUserWithAuthorities()
            .map { UserAccountDTO(it) }
            .orElseThrow { AccountResourceException("User could not be found") }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException `400 (Bad Request)` if the email is already used.
     * @throws RuntimeException `500 (Internal Server Error)` if the user login wasn't found.
     */
    @PostMapping("/account")
    fun saveAccount(@Valid @RequestBody userDTO: UserDTO) {
        val userLogin = getCurrentUserLogin()
            .orElseThrow { AccountResourceException("") }
        val existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.email)
        if (existingUser.isPresent && !existingUser.get().login.equals(userLogin, ignoreCase = true)) {
            throw EmailAlreadyUsedException()
        }
        val user = userRepository.findOneByLogin(userLogin)
        if (!user.isPresent) {
            throw AccountResourceException("User could not be found")
        }
        userService.updateUser(
            userDTO.firstName, userDTO.lastName, userDTO.email,
            userDTO.langKey, userDTO.imageUrl
        )
    }

    /**
     * POST  /account/change-password : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException `400 (Bad Request)` if the new password is incorrect.
     */
    @PostMapping(path = ["/account/change-password"])
    fun changePassword(@RequestBody passwordChangeDto: PasswordChangeDTO) {
        if (!checkPasswordLength(passwordChangeDto.newPassword)) {
            throw InvalidPasswordException()
        }
        userService.changePassword(passwordChangeDto.currentPassword!!, passwordChangeDto.newPassword!!)
    }

    /**
     * POST   /account/reset-password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     */
    @PostMapping(path = ["/account/reset-password/init"])
    fun requestPasswordReset(@RequestBody mail: String) {
        val user = userService.requestPasswordReset(mail)
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get())
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail '$mail'")
        }
    }

    /**
     * `POST   /account/reset-password/finish` : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException `400 (Bad Request)` if the password is incorrect.
     * @throws RuntimeException `500 (Internal Server Error)` if the password could not be reset.
     */
    @PostMapping(path = ["/account/reset-password/finish"])
    fun finishPasswordReset(@RequestBody keyAndPassword: KeyAndPasswordVM) {
        if (!checkPasswordLength(keyAndPassword.newPassword)) {
            throw InvalidPasswordException()
        }
        val user = userService.completePasswordReset(keyAndPassword.newPassword!!, keyAndPassword.key!!)

        if (!user.isPresent) {
            throw AccountResourceException("No user was found for this reset key")
        }
    }
}

private fun checkPasswordLength(password: String?) =
    !password.isNullOrEmpty() &&
        password.length >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
        password.length <= ManagedUserVM.PASSWORD_MAX_LENGTH
