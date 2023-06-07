package cn.jaylong.auth.kt.po

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.TypeDef
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

/**
 * Created by IntelliJ IDEA.
 * Author: I'm
 * Date: 2021/10/15
 */
@Entity
@Table(name = "role", schema = "auth")
@EntityListeners(
    AuditingEntityListener::class
)
@NamedEntityGraph(name = Role.EG_DEFAULT)
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id",
    scope = Role::class
)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class Role(
    @Id
    @Column(length = 64)
    var id: String = "",

    /**
     * 编码
     */
    @Column(nullable = false, unique = true, length = 64)
    var code: String = "",

    /**
     * 描述
     */
    @Column
    var description: String = "",
) {
    companion object {
        const val EG_DEFAULT = "Role.default"
    }
}