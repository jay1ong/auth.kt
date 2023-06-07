package cn.jaylong.auth.kt.repository

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface BaseEntityGraphRepository<T, ID : Serializable> : EntityGraphJpaRepository<T, ID>,
    EntityGraphQuerydslPredicateExecutor<T>