import java.util.Date

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val skipCommitsCount = 0
val versionMajor = 1
val versionMinor = 0
val versionPatch = providers
    .exec {
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText
    .get()
    .trim()
    .toInt()

val versionName = "${versionMajor}.${versionMinor}.${versionPatch - skipCommitsCount}"

fun TaskContainer.registerCopyAarTask(variant: String) {
    val capVariant = variant.replaceFirstChar { it.uppercaseChar() }
    register<Delete>("deleteOld${capVariant}Aar") {
        group = "aar"
        description = "Удаляет ранее собранные AAR в ../aar для $variant"
        delete(fileTree("../aar") {
            include("taobubbles-$variant-*.aar")
        })
    }

    register<Copy>("copy${capVariant}Aar") {
        group = "aar"
        description = "Copy AAR $variant with version $versionName to ../aar"
        dependsOn("assemble${capVariant}")
        dependsOn("deleteOld${capVariant}Aar")
        val aarFile = file("build/outputs/aar/taobubbles-$versionName-$variant.aar")
        doFirst {
            // Создать ../aar если не существует
            file("../aar").mkdirs()
            if (!aarFile.exists()) {
                throw GradleException("AAR file does not exist: $aarFile")
            }
            println("Copying $aarFile to ../aar/taobubbles-$variant.aar")
        }
        from(aarFile)
        into("../aar")
        if(variant == "release") {
            rename { "taobubbles.aar" }
        } else {
            rename { "taobubbles-$variant.aar" }
        }
        doLast {
            val versionFile = file("../aar/README.txt")
            versionFile.writeText("Library: taobubbles\nVersion: $versionName\nCreated: ${Date()}")
            println("Created version file: ${versionFile.absolutePath}")
        }
    }
}

tasks.registerCopyAarTask("release")
tasks.registerCopyAarTask("debug")

android {
    namespace = "ua.at.tsvetkov.bubbles"

    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        setProperty("archivesBaseName", "taobubbles-$versionName")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    debugImplementation(mapOf("name" to "taolog-debug", "ext" to "aar"))
    debugImplementation(mapOf("name" to "taocore-debug", "ext" to "aar"))
    releaseImplementation(mapOf("name" to "taolog", "ext" to "aar"))
    releaseImplementation(mapOf("name" to "taocore", "ext" to "aar"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.activity.compose)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.constraint.layout)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    tasks.named("assembleDebug").configure {
        finalizedBy("copyDebugAar")
    }
    tasks.named("assembleRelease").configure {
        finalizedBy("copyReleaseAar")
    }
    tasks.named("build").configure {
        dependsOn("copyReleaseAar")
        dependsOn("copyDebugAar")
    }
}