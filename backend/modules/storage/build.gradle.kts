plugins {
    `java-library`
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))

    implementation(project(":modules:common"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
