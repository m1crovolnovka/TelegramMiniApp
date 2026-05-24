plugins {
    `java-library`
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))

    implementation(project(":modules:common"))
    implementation(project(":modules:config"))
    implementation(project(":modules:economy"))
    implementation(project(":modules:users"))
    implementation(project(":modules:cards"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
