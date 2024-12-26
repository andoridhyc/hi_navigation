pluginManagement {
    repositories {

        maven ( url = "https://maven.aliyun.com/repository/google")
        maven ( url= "https://maven.aliyun.com/repository/central")
        maven ( url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven ( url = "https://maven.aliyun.com/repository/public")

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        maven ( url = "https://maven.aliyun.com/repository/google")
        maven ( url= "https://maven.aliyun.com/repository/central")
        maven ( url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven ( url = "https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
    }
}

rootProject.name = "hi_navigation"
include(":app")
include(":nav-annotation")
include(":nav-compiler")
