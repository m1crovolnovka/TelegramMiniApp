plugins {
    `java-library`
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))

    implementation(project(":modules:common"))
    implementation(project(":modules:config"))
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework:spring-messaging")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
