package cn.jaylong.auth.kt.po

import cn.jaylong.auth.kt.enum.StatusEnum
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import kotlinx.serialization.Contextual
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2021/9/23
 */
@Entity
@Table(
    name = "user",
    schema = "auth",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_zone_username",
        columnNames = ["zoneId", "username"]
    ), UniqueConstraint(
        name = "uk_zone_email",
        columnNames = ["zoneId", "email"]
    ), UniqueConstraint(name = "uk_zone_phone", columnNames = ["zoneId", "phone_number"])]
)
@EntityListeners(
    AuditingEntityListener::class
)
@NamedEntityGraph(name = User.EG_DEFAULT)
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id",
    scope = User::class
)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class User(
    /**
     * 用户ID
     */
    @Id
    @Column(length = 64) var id: String = "",

    /**
     * 用户域ID
     */
    @Column(length = 64, nullable = false)
    var zoneId: String = "",

    /**
     * 外部ID
     */
    @Column
    var externalId: String = "",

    /**
     * 使用第三方身份源或社会化登录的用户，该字段为用户在第三方的 ID。
     */
    @Column
    var unionId: String = "",

    /**
     * 第三方身份源用户，返回的 openid。
     */
    @Column
    var openId: String = "",
    // endregion
    /**
     * 用户资源描述符
     */
    @Column(length = 128)
    var irn: String = "",

    /**
     * 密码
     */
    @Column
    var password: String = "",

    /**
     * 账户是否过期
     */
    @Column
    var accountNonExpired: Boolean = true,

    /**
     * 账户是否锁定
     */
    @Column
    var accountNonLocked: Boolean = true,

    /**
     * 凭证是否过期
     */
    @Column
    var credentialsNonExpired: Boolean = true,

    /**
     * 用户是否可用
     */
    @Column
    var enabled: Boolean = true,

    /**
     * 昵称
     */
    @Column
    var nickname: String = "",

    /**
     * 用户所属公司
     */
    @Column
    var company: String = "",

    /**
     * 头像地址
     */
    @Column(length = 2048)
    var avatar: String = "",
    // region 兼容OIDC 用户信息，抽离一些信息出来方便操作
    /**
     * 登录账号
     */
    @Column(length = 64)
    var username: String = "",

    /**
     * 邮箱
     */
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "value", column = Column(name = "email")),
        AttributeOverride(name = "verified", column = Column(nullable = false, name = "email_verified"))
    )
    var email: VerifierEmbed? = null,

    /**
     * 手机号码
     */
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "value", column = Column(name = "phone_number")),
        AttributeOverride(name = "verified", column = Column(nullable = false, name = "phone_number_verified"))
    )
    var phoneNumber: VerifierEmbed? = null,

    /**
     * 地址(json)
     */
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    var address: AddressData? = null,
    // endregion
    /**
     * 注册来源，可以多选（导入、来自XX应用注册）
     */
    @Column
    var source: String = "",

    /**
     * 用户状态
     */
    @Column
    var status: StatusEnum = StatusEnum.NORMAL,

    /**
     * 注册信息
     */
    @Embedded
    var registerInfo: RegisterInfoEmbed? = null,

    /**
     * 最近登录信息
     */
    @Embedded
    var lastLoginInfo: LastLoginInfoEmbed? = null,
    // region DB
    /**
     * 创建时间
     */
    @Column
    @CreatedDate
    @Contextual
    var createAt: LocalDateTime = LocalDateTime.now(),

    @Column
    @LastModifiedDate
    @Contextual
    var lastModifiedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        schema = "auth",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")],
        foreignKey = ForeignKey(value = ConstraintMode.NO_CONSTRAINT),
        inverseForeignKey = ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
    )
    var roles: MutableSet<Role>? = null,
) : java.io.Serializable {
    companion object {
        const val EG_DEFAULT = "User.default"
    }
}