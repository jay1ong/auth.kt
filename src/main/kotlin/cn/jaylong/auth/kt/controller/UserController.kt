package cn.jaylong.auth.kt.controller

import cn.jaylong.auth.kt.po.User
import cn.jaylong.auth.kt.repository.UserJpaRepository
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@RestController
@RequestMapping("/user")
class UserController(private var repository: UserJpaRepository) {


    @ApiOperation("根据用户名获取用户信息")
    @GetMapping(
        "/username",
        produces = ["application/json"]
    ) //    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') ")
    fun getUserByUsername(@RequestParam username: String): User {
        return repository.loadUserByUsername(username)
    }
}