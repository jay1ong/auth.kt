package cn.jaylong.auth.kt.controller

import cn.jaylong.auth.kt.dto.UserRequest
import cn.jaylong.auth.kt.exception.AuthException
import cn.jaylong.auth.kt.payload.LoginResponse
import cn.jaylong.auth.kt.repository.UserJpaRepository
import cn.jaylong.auth.kt.security.model.AuthUserDetails
import cn.jaylong.auth.kt.service.TokenService
import cn.jaylong.auth.kt.service.UserService
import cn.jaylong.core.exception.BizException
import io.swagger.annotations.ApiOperation
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Collectors
import javax.validation.Valid

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@RestController
@RequestMapping("/auth")
class AuthorizationController(
    private var userService: UserService,
    private var passwordEncoder: PasswordEncoder,
    private var jpaRepository: UserJpaRepository,
    private var authenticationManager: AuthenticationManager,
    private var tokenService: TokenService,
) {

    @ApiOperation("注册")
    @PostMapping("/register")
    fun register(
        @RequestBody request: UserRequest
    ) {
        val usernameExist: Boolean = jpaRepository.existsByUsername(request.username)
        if (usernameExist) {
            throw BizException(AuthException.USER_EXIST)
        } else {
            userService.saveUser(request.username, request.password)
        }
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    fun login(@RequestBody request: @Valid UserRequest): LoginResponse {
        val loginResponse = LoginResponse()
        var userDetails: AuthUserDetails = userService.loadUserByUsername(request.username)
        if (passwordEncoder.matches(request.password, userDetails.password)) {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            SecurityContextHolder.getContext().authentication = authentication
            val token: String = tokenService.generateToken(authentication)
            userDetails = authentication.principal as AuthUserDetails
            val roles: List<String> = userDetails.getAuthorities().stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .collect(Collectors.toList())
            loginResponse.accessToken = token
            loginResponse.id = userDetails.id
            loginResponse.username = userDetails.username
            loginResponse.email = userDetails.email ?: ""
            loginResponse.roles = roles
        } else {
            throw BizException(AuthException.USER_PASSWORD_ERROR)
        }
        return loginResponse
    }
}