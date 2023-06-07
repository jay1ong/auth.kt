package cn.jaylong.auth.kt.po

import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@Embeddable
data class VerifierEmbed(
    /**
     * 值
     */
    @Column
    var value: String = "",

    /**
     * 是否已验证
     */
    @Column(nullable = false)
    var verified: Boolean = false,

    )