package cn.jaylong.auth.kt.repository

import cn.jaylong.auth.kt.exception.AuthException
import cn.jaylong.auth.kt.po.QUser
import cn.jaylong.auth.kt.po.User
import cn.jaylong.core.exception.BizException
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor
import org.springframework.stereotype.Repository

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/26
 */
@Repository
interface UserJpaRepository : EntityGraphJpaRepository<User, String>, EntityGraphQuerydslPredicateExecutor<User> {
    @JvmDefault
    fun loadUserByUsername(username: String): User {
        return findOne(
            DSL.username.eq(username)
                .or(
                    DSL.email.value.eq(username)
                )
                .or(
                    DSL.phoneNumber.value.eq(username)
                )
        )
            .orElseThrow { BizException(AuthException.USER_NOT_EXIST) }
    }

    fun existsByUsername(username: String): Boolean

    companion object {
        val DSL: QUser = QUser.user
    }
}