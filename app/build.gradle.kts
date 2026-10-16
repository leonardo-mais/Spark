import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val versionPropsFile = rootProject.file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        load(versionPropsFile.inputStream())
    }
}

val propAppName = versionProps.getProperty("appName", "Spark")
val propVersionCode = versionProps.getProperty("versionCode", "1").toInt()
val propVersionName = versionProps.getProperty("versionName", "1.0.0")

android {
    namespace = "it.spark.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.spark.app"
        minSdk = 26
        targetSdk = 35
        versionCode = propVersionCode
        versionName = propVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val envKeystorePath = System.getenv("KEYSTORE_PATH")
            val defaultKeystore = file("release-key.jks")
            val customKeystore = envKeystorePath?.let { file(it) }

            val targetKeystore = when {
                customKeystore?.exists() == true -> customKeystore
                defaultKeystore.exists() -> defaultKeystore
                else -> null
            }

            if (targetKeystore != null) {
                storeFile = targetKeystore
                storePassword = requireNotNull(System.getenv("KEYSTORE_PASSWORD")) {
                    "KEYSTORE_PASSWORD must be set to sign release builds"
                }
                keyAlias = System.getenv("KEY_ALIAS") ?: "sparkkey"
                keyPassword = requireNotNull(System.getenv("KEY_PASSWORD")) {
                    "KEY_PASSWORD must be set to sign release builds"
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val releaseSigning = signingConfigs.getByName("release")
            signingConfig = if (releaseSigning.storeFile != null) releaseSigning else signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3) {
        version {
            strictly(libs.versions.material3.get())
        }
    }
    implementation(libs.compose.material.icons.extended)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}

android.applicationVariants.all {
    val variant = this
    val variantName = variant.name.replaceFirstChar { it.uppercase() }
    val buildTypeName = variant.buildType.name

    val copyTask = tasks.register("copy${variantName}ApkToRoot") {
        doLast {
            val searchDirs = listOf(
                layout.buildDirectory.dir("outputs/apk/$buildTypeName").get().asFile,
                File(project.buildDir, "outputs/apk/$buildTypeName")
            )
            var copied = false
            for (dir in searchDirs) {
                if (dir.exists()) {
                    val apkFile = dir.listFiles()?.firstOrNull { it.name.endsWith(".apk") && !it.name.contains("unsigned") }
                        ?: dir.listFiles()?.firstOrNull { it.name.endsWith(".apk") }
                    if (apkFile != null) {
                        val targetDir = rootProject.file("apk")
                        targetDir.mkdirs()
                        val targetFile = File(targetDir, "$propAppName-v$propVersionName-b$propVersionCode-$buildTypeName.apk")
                        apkFile.copyTo(targetFile, overwrite = true)
                        println("📦 [copyApkToRoot] Successfully placed APK: ${targetFile.absolutePath} (${targetFile.length()} bytes)")
                        copied = true
                        break
                    }
                }
            }
            if (!copied) {
                println("⚠️ [copyApkToRoot] Could not find generated APK in search directories")
            }
        }
    }
    variant.assembleProvider.get().finalizedBy(copyTask)
}
