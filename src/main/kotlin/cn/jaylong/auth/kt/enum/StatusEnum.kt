package cn.jaylong.auth.kt.enum

import cn.hutool.core.util.StrUtil
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/26
 */
enum class StatusEnum(private val code: String, private val message: String) {
    NORMAL("normal", "正常"),
    DISABLED("disabled", "禁用"),
    LOCK("lock", "锁定"),
    EXPIRED("expired", "过期");

    companion object {
        fun getStatusByCode(code: String?): StatusEnum {
            return Arrays.stream(StatusEnum.values())
                .filter { statusEnum: StatusEnum ->
                    StrUtil.equals(
                        statusEnum.code,
                        code
                    )
                }.findFirst().orElse(null)
        }
    }
}