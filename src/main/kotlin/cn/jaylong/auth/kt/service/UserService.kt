package cn.jaylong.auth.kt.service

import cn.hutool.core.util.RandomUtil
import cn.jaylong.auth.kt.po.User
import cn.jaylong.auth.kt.po.VerifierEmbed
import cn.jaylong.auth.kt.repository.UserJpaRepository
import cn.jaylong.auth.kt.security.model.AuthUserDetails
import org.springframework.context.ApplicationContext
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@Service
class UserService(
    private var jpaRepository: UserJpaRepository,
    private var context: ApplicationContext,
) : UserDetailsService {


    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): AuthUserDetails {
        return AuthUserDetails.build(jpaRepository.loadUserByUsername(username))
    }

    fun saveUser(username: String, password: String) {
        val user = User()
        val random = RandomUtil.randomNumbers(32)
        user.id = random
        user.username = username
        user.password = (context.getBean("passwordEncoder") as PasswordEncoder).encode(password)
        user.zoneId = "1"
        val embed = VerifierEmbed()
        embed.value = random
        user.email = embed
        user.phoneNumber = embed
        jpaRepository.save(user)
    }
}