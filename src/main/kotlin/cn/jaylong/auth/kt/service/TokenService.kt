package cn.jaylong.auth.kt.service

import cn.hutool.core.convert.Convert
import cn.jaylong.auth.kt.exception.AuthException
import cn.jaylong.auth.kt.util.logger
import cn.jaylong.core.exception.BizException
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Service
import java.io.IOException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.text.ParseException
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2023/5/27
 */
@Service
@Slf4j
class TokenService : AuthenticationEntryPoint {
    @Value("\${jwt.private.key}")
    var privateKey: RSAPrivateKey? = null

    @Value("\${jwt.public.key}")
    var publicKey: RSAPublicKey? = null
    fun generateToken(authentication: Authentication): String {
        val now = Instant.now()
        val expiry = 36000L
        // @formatter:off
        val scope = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(" "))
        val claims = JWTClaimsSet.Builder()
            .issuer("self")
            .issueTime(Date(now.toEpochMilli()))
            .expirationTime(Date(now.plusSeconds(expiry).toEpochMilli()))
            .subject(authentication.name)
            .claim("scope", scope)
            .build()
        // @formatter:on
        val header = JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT).build()
        val jwt = SignedJWT(header, claims)
        return sign(jwt).serialize()
    }

    fun sign(jwt: SignedJWT): SignedJWT {
        return try {
            jwt.sign(RSASSASigner(privateKey))
            jwt
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex)
        }
    }

    @Throws(JOSEException::class)
    fun validateToken(authToken: String?): Boolean {
        val jwsObject: JWSObject
        jwsObject = try {
            JWSObject.parse(authToken)
        } catch (e: ParseException) {
            throw BizException(AuthException.TOKEN_PARSING_ERROR)
        }
        val jwsVerifier: JWSVerifier = RSASSAVerifier(publicKey)
        if (!jwsObject.verify(jwsVerifier)) {
            throw BizException(AuthException.INVALID_TOKEN)
        }
        val map = jwsObject.payload.toJSONObject()
        val exp = Date(Convert.toLong(map["exp"]) * 1000).time
        val now = Date(Instant.now().toEpochMilli()).time
        if (exp < now) {
            throw BizException(AuthException.TOKEN_EXPIRED)
        }
        return true
    }

    fun getUserNameFromToken(authToken: String?): String {
        val map: Map<String, Any>
        map = try {
            JWSObject.parse(authToken).payload.toJSONObject()
        } catch (e: ParseException) {
            throw BizException(AuthException.TOKEN_PARSING_USERNAME_ERROR)
        }
        return Convert.toStr(map["sub"])
    }

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger().error("鉴权失败: {}", authException.message)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val body: MutableMap<String, Any?> = HashMap()
        body["status"] = HttpServletResponse.SC_UNAUTHORIZED
        body["error"] = "未鉴权"
        body["message"] = authException.message
        body["path"] = request.servletPath
        val mapper = ObjectMapper()
        mapper.writeValue(response.outputStream, body)
    }
}