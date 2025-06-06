plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.omsi.toneplayer"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") { // Use create<MavenPublication>("name")
                groupId = "com.android.lib"
                artifactId = "custom-wav-player"
                version = "1.0.0"

                // For Android libraries, 'release' component is usually the one you want.
                // If it's a generic Java/Kotlin library, it might be 'java'.
                from(components["release"]) // Access components by name like a map
            }
            // You can define other publications here if needed, e.g., for debug
            /*
            create<MavenPublication>("debug") {
                groupId = "com.android.lib"
                artifactId = "custom-tone-maker-debug" // Often good to differentiate artifactId for debug
                version = "1.0.2-DEBUG"
                from(components["debug"])
            }
            */
        }

        repositories {
            maven {
                name = "myRepo"
                url = uri("C:/projects/AndroidStudio/repos/mvn-repo-public") // Use forward slashes for paths or escape backslashes

            }
            // You can add other repositories here, like Maven Central, etc.
            /*
            mavenCentral()
            */
        }
    }
}