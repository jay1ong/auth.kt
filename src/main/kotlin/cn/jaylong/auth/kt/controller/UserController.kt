package cn.jaylong.auth.kt.controller

import cn.jaylong.auth.kt.exception.AuthException
import cn.jaylong.auth.kt.po.User
import cn.jaylong.auth.kt.repository.UserJpaRepository
import cn.jaylong.auth.kt.service.UserService
import cn.jaylong.core.exception.BizException
import io.swagger.annotations.ApiOperation
import org.springframework.context.ApplicationContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@RestController
@RequestMapping("/user")
class UserController(
    private var repository: UserJpaRepository,
    private var service: UserService,
    private var context: ApplicationContext,
) {

    @ApiOperation("根据用户名获取用户信息")
    @GetMapping("/username") //    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') ")
    fun getByUsername(@RequestParam username: String): User {
        return repository.loadUserByUsername(username)
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    fun list(): List<User> {
        return repository.findAll()
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun delete(@RequestBody ids: List<String>) {
        repository.deleteAllById(ids)
    }

    @ApiOperation("新增用户")
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun add(@RequestBody user: User) {
        val usernameExist: Boolean = repository.existsByUsername(user.username)
        if (usernameExist) {
            throw BizException(AuthException.USER_EXIST)
        } else {
            service.saveUser(user.username, user.password)
        }
    }

    @ApiOperation("编辑用户")
    @PutMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun change(@RequestBody user: User) {
        val po = repository.findById(user.id).orElseThrow { BizException(AuthException.USER_NOT_EXIST) }
        po.username = user.username
        po.password = (context.getBean("passwordEncoder") as PasswordEncoder).encode(user.password)
        repository.save(po)
    }

}