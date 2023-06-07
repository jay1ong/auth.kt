package cn.jaylong.auth.kt.payload

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
data class LoginResponse(
    var accessToken: String = "",
    var type: String = "Bearer",
    var id: String = "",
    var username: String = "",
    var email: String = "",
    var roles: List<String> = emptyList(),
)