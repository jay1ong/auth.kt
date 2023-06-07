package cn.jaylong.auth.kt.po

import kotlinx.serialization.Contextual
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class LastLoginInfoEmbed(
    /**
     * 上次登录时使用的浏览器(UA信息)
     *
     * user-agent
     */
    @Column(length = 64)
    var ua: String = "",

    /**
     * 登录设备
     */
    @Column(length = 64)
    var device: String = "",

    /**
     * 最近一次登录IP
     */
    @Column(length = 16)
    var lastLoginIp: String = "",

    /**
     * 最近一次登录时间
     */
    @Column
    @Contextual
    var lastLoginAt: LocalDateTime = LocalDateTime.now(),

    /**
     * 登录次数
     */
    @Column(length = 64)
    var loginCount: Long = 0
) : Serializable