plugins {
    `java-library`
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.2"))

    implementation(project(":modules:common"))
    implementation(project(":modules:config"))
    implementation(project(":modules:auth"))
    implementation(project(":modules:users"))
    implementation(project(":modules:economy"))
    implementation(project(":modules:cards"))
    implementation(project(":modules:packs"))
    implementation(project(":modules:casino"))
    implementation(project(":modules:quests"))
    implementation(project(":modules:betting"))
    implementation(project(":modules:trades"))
    implementation(project(":modules:notifications"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
