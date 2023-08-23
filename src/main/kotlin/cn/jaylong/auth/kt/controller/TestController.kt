package cn.jaylong.auth.kt.controller

import cn.hutool.core.util.RandomUtil
import cn.jaylong.auth.kt.dto.UserRequest
import cn.jaylong.auth.kt.exception.AuthException
import cn.jaylong.auth.kt.payload.LoginResponse
import cn.jaylong.auth.kt.po.User
import cn.jaylong.auth.kt.po.VerifierEmbed
import cn.jaylong.auth.kt.repository.UserJpaRepository
import cn.jaylong.auth.kt.security.model.AuthUserDetails
import cn.jaylong.auth.kt.service.TokenService
import cn.jaylong.auth.kt.service.UserService
import cn.jaylong.core.exception.BizException
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Hex
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors
import javax.validation.Valid

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@RestController
@Api(tags = ["测试模块"])
@RequestMapping("/test")
class TestController(
    private var jpaRepository: UserJpaRepository,
    private var userService: UserService,
    private var passwordEncoder: PasswordEncoder,
    private var authenticationManager: AuthenticationManager,
    private var tokenService: TokenService,
) {


    @Value("\${app.name}")
    var appName: String? = null

    @ApiOperation("测试app name")
    @GetMapping("/value")
    fun value(): String? {
        return appName
    }

    @ApiOperation("测试保存user数据")
    @GetMapping("/save")
    @Transactional
    fun insert(
        @RequestParam(required = false) id: String,
        @RequestParam(required = false) username: String,
        @RequestParam(required = false) password: String,
        @RequestParam(required = false) roles: String,
        @RequestParam(required = false) email: String,
        @RequestParam(required = false) phoneNumber: String
    ) {
        // 更新
        var user = User()
        if (id.isNotBlank()) {
            user = jpaRepository.findById(id).orElse(User())
        }
        val random = RandomUtil.randomNumbers(32)
        user.id = id.ifBlank { random }
        user.username = username
        user.password = passwordEncoder.encode(password)
        user.zoneId = "1"
        val embed = VerifierEmbed()
        embed.value = email.ifBlank { random }
        user.email = embed
        embed.value = phoneNumber.ifBlank { random }
        user.phoneNumber = embed
        jpaRepository.save(user)
    }

    @ApiOperation("测试删除user数据")
    @GetMapping("/delete")
    fun delete(@RequestParam id: String) {
        val user: User = jpaRepository.findById(id).orElse(User())
        jpaRepository.delete(user)
    }

    @ApiOperation("测试查询user数据")
    @GetMapping("/query")
    fun query(@RequestParam id: String): User {
        return jpaRepository.findById(id).orElse(User())
    }

    @ApiOperation("测试根据用户名获取用户")
    @GetMapping("/loadUserByUsername")
    fun loadUserByUsername(@RequestParam username: String): UserDetails {
        return userService.loadUserByUsername(username)
    }

    @ApiOperation("测试登录")
    @PostMapping("/login")
    fun query(
        @RequestBody request: @Valid UserRequest
    ): LoginResponse {
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
            loginResponse.email = userDetails.email
            loginResponse.roles = roles
        } else {
            throw BizException(AuthException.USER_PASSWORD_ERROR)
        }
        return loginResponse
    }

    companion object {
        @JvmStatic
        fun main(strings: Array<String>) {
            val base32Key = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK"
            val base32 = Base32()
            val b = base32.decode(base32Key)
            val secret = Hex.encodeHexString(b)
            println(secret)
        }
    }
}