plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "StockSim"
include("modules:domain")
include("modules:repository")
include("modules:repository-data")
include("modules:repository-jpa")
include("modules:host")
include("modules:http")
include("modules:services")
