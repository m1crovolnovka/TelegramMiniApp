pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "casino-backend"

include(
    "app",
    "modules:common",
    "modules:config",
    "modules:storage",
    "modules:websocket",
    "modules:auth",
    "modules:users",
    "modules:economy",
    "modules:cards",
    "modules:packs",
    "modules:casino",
    "modules:quests",
    "modules:betting",
    "modules:trades",
    "modules:admin",
    "modules:notifications",
    "quest-bot-app",
)
