package cn.jaylong.auth.kt.security.model

import cn.jaylong.auth.kt.po.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AuthUserDetails(
    @JvmField val id: String,
    @JvmField val username: String,
    @JvmField val email: String,
    @JvmField @field:JsonIgnore val password: String,
    @JvmField val authorities: Collection<GrantedAuthority>
) : org.springframework.security.core.userdetails.UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        private const val serialVersionUID = -1997332321465493976L
        fun build(user: User): AuthUserDetails {
            val authorities: List<SimpleGrantedAuthority>? =
                user.roles?.map { role -> SimpleGrantedAuthority(role.code) }
            return AuthUserDetails(
                user.id,
                user.username,
                user.email?.value ?: "",
                user.password,
                authorities ?: emptyList()
            )
        }
    }
}
