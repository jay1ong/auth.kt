package cn.jaylong.auth.kt.po

import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class RegisterInfoEmbed(
    /**
     * 注册IP
     *
     * len(255.255.255.255) == 15
     */
    @Column(length = 16)
    var registerIpv4: String = "",

    /**
     * 注册Ipv6表示
     *
     *
     * len(0001:0002:0003:0004:0005:0006:0007:0008) == 39
     * len(0001:0002:0003:0004:0005:ffff:255.255.255.255) == 45
     *
     */
    @Column(length = 46)
    var registerIpv6: String = "",

    /**
     * 注册时的时间
     */
    @Column
    @Contextual
    var registerAt: LocalDateTime = LocalDateTime.now(),

    )