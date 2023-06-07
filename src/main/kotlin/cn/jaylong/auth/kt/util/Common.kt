package cn.jaylong.auth.kt.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
inline fun <reified T> T.logger() : Logger {
    return LoggerFactory.getLogger(T::class.java)
}