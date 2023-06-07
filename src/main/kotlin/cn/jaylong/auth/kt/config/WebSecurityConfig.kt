package cn.jaylong.auth.kt.config

import cn.jaylong.auth.kt.security.jwt.AuthTokenFilter
import cn.jaylong.auth.kt.service.TokenService
import cn.jaylong.auth.kt.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.security.interfaces.RSAPublicKey

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2021/9/28
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private var authenticationJwtTokenFilter: AuthTokenFilter,
    private var userService: UserService,
    private var tokenService: TokenService,
) : WebSecurityConfigurerAdapter() {
    @Value("\${jwt.public.key}")
    var key: RSAPublicKey? = null

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/v2/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security"
        )
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(tokenService).and()
            .headers().frameOptions().disable()
            .and()
            .authorizeRequests()
            .antMatchers("/", "/favicon.ico", "/auth/login", "/auth/register").permitAll()
            .antMatchers("/swagger-ui", "/swagger-ui/**", "/test/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .logout().permitAll()
            .and()
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(key).build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // 密码加密方式
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    public override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder.userDetailsService<UserDetailsService?>(userService)
            .passwordEncoder(passwordEncoder())
    }
}