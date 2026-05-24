plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:config"))
    implementation(project(":modules:users"))
    implementation(project(":modules:quests"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
