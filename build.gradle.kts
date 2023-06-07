import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    id("org.springframework.boot") version "2.7.12"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"

    kotlin("plugin.serialization") version "1.8.21"
    kotlin("plugin.jpa") version "1.8.21"
    id("idea")
    kotlin("kapt") version "1.8.21"
    application
    kotlin("plugin.allopen") version "1.3.61"
}

tasks.processResources {
    // https://github.com/gradle/kotlin-dsl-samples/blob/master/samples/copy/build.gradle.kts
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val tokens = mapOf("name" to project.name)
    inputs.properties(tokens)
    from("src/main/resources")
    into("build/target/resources")
    filesMatching("**/*.yml") {
        filter<ReplaceTokens>("tokens" to tokens)
    }
    // convert from java
//    project.properties.forEach {
//        filesMatching("**/*.yml") {
//            if (it.value != null && it.value is String) {
//                val tokens = mapOf(
//                    it.key to it.value
//                )
//                filter(
//                    mapOf(
//                        "tokens" to tokens
//                    ), ReplaceTokens::class.java
//                )
//                val tokens1 = mapOf(
//                    it.key to it.value
//                )
//                filter(
//                    mapOf(
//                        "tokens" to tokens1
//                    ), ReplaceTokens::class.java
//                )
//            }
//        }
//    }
}

sourceSets {
    main {
        kotlin {
            exclude("src/main/java/**")
            exclude("$buildDir/generated/sources")
        }
    }
}

val repoUrl: String by project
val repoUsername: String by project
val repoPassword: String by project

group = "cn.jaylong"
version = "2021.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    maven {
        url = uri(repoUrl)
        credentials {
            username = repoUsername
            password = repoPassword
        }
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central/")
    }
    mavenCentral()
    mavenLocal()
}

val springcloudVersion = "2020.0.3"
val nimbusjosejwtVersion = "9.15.2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(platform("cn.jaylong:lab-dependencies-gradle-kt:$version"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:$springcloudVersion"))
    annotationProcessor(platform("cn.jaylong:lab-dependencies-gradle-kt:$version"))
    implementation("cn.jaylong:lab-data-jpa-gradle-kt")
    implementation("cn.jaylong:lab-autoconfig-gradle-kt")

    annotationProcessor("cn.jaylong:lab-autoconfig-gradle-kt")
//    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("javax.annotation:javax.annotation-api")
    annotationProcessor("org.hibernate.javax.persistence:hibernate-jpa-2.1-api")

    implementation("com.nimbusds:nimbus-jose-jwt:$nimbusjosejwtVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
    kapt("com.querydsl:querydsl-apt:5.0.0:general")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embedded")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict","-Xjvm-default=enable")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClass.set("cn.jaylong.auth.kt.Application")
}