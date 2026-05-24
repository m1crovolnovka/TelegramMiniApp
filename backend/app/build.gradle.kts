plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:config"))
    implementation(project(":modules:storage"))
    implementation(project(":modules:websocket"))
    implementation(project(":modules:auth"))
    implementation(project(":modules:users"))
    implementation(project(":modules:economy"))
    implementation(project(":modules:cards"))
    implementation(project(":modules:packs"))
    implementation(project(":modules:casino"))
    implementation(project(":modules:quests"))
    implementation(project(":modules:betting"))
    implementation(project(":modules:trades"))
    implementation(project(":modules:admin"))
    implementation(project(":modules:notifications"))
    implementation(project(":quest-bot-app"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
