plugins {
    `java-library`
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))
    api("jakarta.persistence:jakarta.persistence-api")
    api("org.hibernate.orm:hibernate-core")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
